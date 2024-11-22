package gg.loto.party;

import gg.loto.character.domain.Characters;
import gg.loto.character.service.CharactersService;
import gg.loto.global.auth.dto.SessionUser;
import gg.loto.party.domain.Party;
import gg.loto.party.domain.PartyType;
import gg.loto.party.mapper.PartyMapper;
import gg.loto.party.repository.PartyRepository;
import gg.loto.party.service.PartyService;
import gg.loto.party.web.dto.PartyJoinRequest;
import gg.loto.party.web.dto.PartyResponse;
import gg.loto.party.web.dto.PartySaveRequest;
import gg.loto.party.web.dto.PartyUpdateRequest;
import gg.loto.user.domain.User;
import gg.loto.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartyServiceUnitTest {

    @InjectMocks
    private PartyService partyService;

    @Mock
    private PartyRepository partyRepository;

    @Mock
    private UserService userService;

    @Mock
    private CharactersService characterService;

    @Mock
    private PartyMapper partyMapper;

    @Nested
    @DisplayName("공유방 생성 기능")
    class createParty{
        @Test
        @DisplayName("공유방 생성이 정상적으로 동작한다")
        void success() {
            // given
            User user = User.builder()
                    .nickname("테스트유저")
                    .discordUsername("test#1234")
                    .build();
            SessionUser sessionUser = new SessionUser(user);
            PartySaveRequest request = PartySaveRequest.builder()
                    .name("테스트파티")
                    .capacity(10)
                    .partyType("FRIENDLY")
                    .build();

            Party party = request.toEntity(user);

            given(userService.getCurrentUser(any())).willReturn(user);
            given(partyRepository.findByNameAndUserId(any(), any())).willReturn(Optional.empty());
            given(partyRepository.save(any())).willReturn(party);

            // when
            PartyResponse response = partyService.createParty(sessionUser, request);

            // then
            assertThat(response.getName()).isEqualTo(request.getName());
            assertThat(response.getCapacity()).isEqualTo(request.getCapacity());
            assertThat(response.getPartyType()).isEqualTo(PartyType.FRIENDLY.getTypeKor());

            verify(partyRepository).findByNameAndUserId(request.getName(), user.getId());
            verify(partyRepository).save(any(Party.class));
        }

        @Test
        @DisplayName("사용자가 생성한 동일한 이름의 공유방이 존재하면 예외가 발생한다")
        void createPartyWithDuplicateName() {
            // given
            User user = User.builder()
                    .nickname("테스트유저")
                    .discordUsername("test#1234")
                    .build();
            SessionUser sessionUser = new SessionUser(user);
            PartySaveRequest request = PartySaveRequest.builder()
                    .name("테스트파티")
                    .capacity(10)
                    .partyType("FRIENDLY")
                    .build();

            given(userService.getCurrentUser(any())).willReturn(user);
            given(partyRepository.findByNameAndUserId(any(), any()))
                    .willReturn(Optional.of(request.toEntity(user)));

            // when & then
            assertThatThrownBy(() -> partyService.createParty(sessionUser, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이미 존재하는 공유방입니다.");
        }
    }

    @Nested
    @DisplayName("공유방 수정 기능")
    class updateParty{
        @Test
        @DisplayName("공유방 정보 수정이 정상적으로 동작한다")
        void success() {
            // given
            Long partyId = 1L;
            User user = User.builder()
                    .nickname("테스트유저")
                    .discordUsername("test#1234")
                    .build();
            ReflectionTestUtils.setField(user, "id", 1L);

            Party party = Party.builder()
                    .user(user)
                    .name("기존파티")
                    .capacity(5)
                    .partyType(PartyType.FRIENDLY)
                    .build();
            ReflectionTestUtils.setField(party, "id", partyId);

            PartyUpdateRequest updateRequest = PartyUpdateRequest.builder()
                    .name("수정된파티")
                    .capacity(10)
                    .partyType("FRIENDLY")
                    .build();

            SessionUser sessionUser = new SessionUser(user);

            given(userService.getCurrentUser(sessionUser)).willReturn(user);
            given(partyRepository.findById(partyId)).willReturn(Optional.of(party));

            // when
            PartyResponse response = partyService.updateParty(sessionUser, partyId, updateRequest);

            // then
            assertThat(response.getName()).isEqualTo(updateRequest.getName());
            assertThat(response.getCapacity()).isEqualTo(updateRequest.getCapacity());
            assertThat(response.getPartyType()).isEqualTo(PartyType.FRIENDLY.getTypeKor());

            verify(userService).getCurrentUser(sessionUser);
            verify(partyRepository).findById(partyId);
        }

        @Test
        @DisplayName("존재하지 않는 공유방 ID로 수정 요청시 예외가 발생한다")
        void updatePartyWithInvalidId() {
            // given
            Long invalidPartyId = 999L;
            User user = User.builder()
                    .nickname("테스트유저")
                    .discordUsername("test#1234")
                    .build();

            PartyUpdateRequest updateRequest = PartyUpdateRequest.builder()
                    .name("수정된파티")
                    .capacity(10)
                    .partyType("FRIENDLY")
                    .build();

            SessionUser sessionUser = new SessionUser(user);

            given(userService.getCurrentUser(sessionUser)).willReturn(user);
            given(partyRepository.findById(invalidPartyId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    partyService.updateParty(sessionUser, invalidPartyId, updateRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("잘못된 공유방 번호 입니다.");
        }

        @Test
        @DisplayName("공유방 소유자가 아닌 사용자가 수정 요청시 예외가 발생한다")
        void updatePartyWithoutOwnership() {
            // given
            Long partyId = 1L;
            User owner = User.builder()
                    .nickname("파티장")
                    .discordUsername("owner#1234")
                    .build();
            ReflectionTestUtils.setField(owner, "id", 1L);

            User otherUser = User.builder()
                    .nickname("다른유저")
                    .discordUsername("other#1234")
                    .build();
            ReflectionTestUtils.setField(otherUser, "id", 2L);

            Party party = Party.builder()
                    .user(owner)
                    .name("기존파티")
                    .capacity(5)
                    .partyType(PartyType.FRIENDLY)
                    .build();
            ReflectionTestUtils.setField(party, "id", partyId);

            PartyUpdateRequest updateRequest = PartyUpdateRequest.builder()
                    .name("수정된파티")
                    .capacity(10)
                    .partyType("FRIENDLY")
                    .build();

            SessionUser sessionUser = new SessionUser(otherUser);

            given(userService.getCurrentUser(sessionUser)).willReturn(otherUser);
            given(partyRepository.findById(partyId)).willReturn(Optional.ofNullable(party));

            // when & then
            assertThatThrownBy(() ->
                    partyService.updateParty(sessionUser, partyId, updateRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("권한이 없는 요청입니다.");
        }
    }

    @Nested
    @DisplayName("방장 권한 위임 기능")
    class transferLeadership {
        @Test
        @DisplayName("방장 권한 위임이 정상적으로 동작한다")
        void success() {
            // given
            Long partyId = 1L;
            User currentLeader = User.builder()
                    .nickname("현재방장")
                    .discordUsername("leader#1234")
                    .build();
            ReflectionTestUtils.setField(currentLeader, "id", 1L);

            User newLeader = User.builder()
                    .nickname("새방장")
                    .discordUsername("newleader#1234")
                    .build();
            ReflectionTestUtils.setField(newLeader, "id", 2L);

            Party party = Party.builder()
                    .user(currentLeader)
                    .name("테스트파티")
                    .capacity(5)
                    .partyType(PartyType.FRIENDLY)
                    .build();
            ReflectionTestUtils.setField(party, "id", partyId);

            SessionUser sessionUser = new SessionUser(currentLeader);

            given(userService.getCurrentUser(sessionUser)).willReturn(currentLeader);
            given(partyRepository.findById(partyId)).willReturn(Optional.of(party));
            given(userService.findById(newLeader.getId())).willReturn(newLeader);

            // when
            PartyResponse response = partyService.transferLeadership(sessionUser, partyId, newLeader.getId());

            // then
            assertThat(response.getNickname()).isEqualTo(newLeader.getNickname());
            verify(userService).getCurrentUser(sessionUser);
            verify(partyRepository).findById(partyId);
            verify(userService).findById(newLeader.getId());
        }

        @Test
        @DisplayName("존재하지 않는 공유방 ID로 권한 위임 요청시 예외가 발생한다")
        void transferLeadershipWithInvalidPartyId() {
            // given
            Long invalidPartyId = 999L;
            User currentLeader = User.builder()
                    .nickname("현재방장")
                    .discordUsername("leader#1234")
                    .build();
            ReflectionTestUtils.setField(currentLeader, "id", 1L);

            SessionUser sessionUser = new SessionUser(currentLeader);
            Long newLeaderId = 2L;

            given(userService.getCurrentUser(sessionUser)).willReturn(currentLeader);
            given(partyRepository.findById(invalidPartyId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    partyService.transferLeadership(sessionUser, invalidPartyId, newLeaderId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("잘못된 공유방 번호 입니다.");
        }

        @Test
        @DisplayName("방장이 아닌 사용자가 권한 위임 요청시 예외가 발생한다")
        void transferLeadershipWithoutOwnership() {
            // given
            Long partyId = 1L;
            User currentLeader = User.builder()
                    .nickname("현재방장")
                    .discordUsername("leader#1234")
                    .build();
            ReflectionTestUtils.setField(currentLeader, "id", 1L);

            User otherUser = User.builder()
                    .nickname("다른유저")
                    .discordUsername("other#1234")
                    .build();
            ReflectionTestUtils.setField(otherUser, "id", 2L);

            Party party = Party.builder()
                    .user(currentLeader)
                    .name("테스트파티")
                    .capacity(5)
                    .partyType(PartyType.FRIENDLY)
                    .build();
            ReflectionTestUtils.setField(party, "id", partyId);

            SessionUser sessionUser = new SessionUser(otherUser);
            Long newLeaderId = 3L;

            given(userService.getCurrentUser(sessionUser)).willReturn(otherUser);
            given(partyRepository.findById(partyId)).willReturn(Optional.of(party));

            // when & then
            assertThatThrownBy(() ->
                    partyService.transferLeadership(sessionUser, partyId, newLeaderId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("권한이 없는 요청입니다.");
        }

        @Test
        @DisplayName("존재하지 않는 사용자에게 권한 위임 요청시 예외가 발생한다")
        void transferLeadershipToInvalidUser() {
            // given
            Long partyId = 1L;
            Long invalidUserId = 999L;
            User currentLeader = User.builder()
                    .nickname("현재방장")
                    .discordUsername("leader#1234")
                    .build();
            ReflectionTestUtils.setField(currentLeader, "id", 1L);

            Party party = Party.builder()
                    .user(currentLeader)
                    .name("테스트파티")
                    .capacity(5)
                    .partyType(PartyType.FRIENDLY)
                    .build();
            ReflectionTestUtils.setField(party, "id", partyId);

            SessionUser sessionUser = new SessionUser(currentLeader);

            given(userService.getCurrentUser(sessionUser)).willReturn(currentLeader);
            given(partyRepository.findById(partyId)).willReturn(Optional.of(party));
            given(userService.findById(invalidUserId))
                    .willThrow(new IllegalArgumentException("회원정보를 찾을 수 없습니다."));

            // when & then
            assertThatThrownBy(() ->
                    partyService.transferLeadership(sessionUser, partyId, invalidUserId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("회원정보를 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("공유방 참여 테스트")
    class JoinParty{
        @Test
        @DisplayName("공유방 방장 참여 성공")
        void joinParty_Success() {
            // given
            User user = User.builder()
                    .email("test@test.com")
                    .nickname("tester")
                    .build();
            ReflectionTestUtils.setField(user, "id", 1L);
            SessionUser sessionUser = new SessionUser(user);

            Party party = Party.builder()
                    .name("테스트파티")
                    .capacity(4)
                    .user(user)
                    .partyType(PartyType.FRIENDLY)
                    .build();
            ReflectionTestUtils.setField(party, "id", 1L);

            List<Long> characterIds = List.of(1L, 2L);
            List<Characters> characters = new ArrayList<>();
            Characters character1 = Characters.builder().user(user).characterName("테스트캐릭터1").build();
            Characters character2 = Characters.builder().user(user).characterName("테스트캐릭터2").build();
            ReflectionTestUtils.setField(character1, "id", 1L);
            ReflectionTestUtils.setField(character2, "id", 2L);
            characters.add(character1);
            characters.add(character2);

            PartyJoinRequest request = PartyJoinRequest.builder()
                    .characters(characterIds)
                    .build();

            // when
            when(userService.getCurrentUser(sessionUser)).thenReturn(user);
            when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
            when(partyMapper.isAlreadyJoinedUser(party.getId(), user.getId())).thenReturn(false);
            when(partyMapper.getJoinedMemberSize(party.getId())).thenReturn(2);
            when(characterService.findAllById(characterIds)).thenReturn(characters);
            when(partyMapper.isAlreadyJoinedCharacter(party.getId(), characterIds)).thenReturn(false);

            // then
            PartyResponse response = partyService.joinParty(sessionUser, 1L, request);

            assertAll(
                    () -> assertEquals(party.getId(), response.getId()),
                    () -> assertEquals(party.getName(), response.getName()),
                    () -> verify(partyMapper).isAlreadyJoinedUser(party.getId(), user.getId()),
                    () -> verify(partyMapper).getJoinedMemberSize(party.getId()),
                    () -> verify(characterService).findAllById(characterIds),
                    () -> verify(partyMapper).isAlreadyJoinedCharacter(party.getId(), characterIds)
            );
        }

        @Test
        @DisplayName("다른 유저 공유방 참여 성공")
        void joinParty_anotherUser_Success() {
            // given
            User owner = User.builder()
                    .email("test@test.com")
                    .nickname("tester")
                    .build();
            ReflectionTestUtils.setField(owner, "id", 1L);

            User another = User.builder()
                    .email("test2@test.com")
                    .nickname("tester2")
                    .build();
            ReflectionTestUtils.setField(another, "id", 1L);
            SessionUser sessionUser = new SessionUser(another);

            Party party = Party.builder()
                    .name("테스트파티")
                    .capacity(4)
                    .user(owner)
                    .partyType(PartyType.FRIENDLY)
                    .build();
            ReflectionTestUtils.setField(party, "id", 1L);

            List<Long> characterIds = List.of(1L, 2L);
            List<Characters> characters = new ArrayList<>();
            Characters character1 = Characters.builder().user(another).characterName("테스트캐릭터1").build();
            Characters character2 = Characters.builder().user(another).characterName("테스트캐릭터2").build();
            ReflectionTestUtils.setField(character1, "id", 1L);
            ReflectionTestUtils.setField(character2, "id", 2L);
            characters.add(character1);
            characters.add(character2);

            PartyJoinRequest request = PartyJoinRequest.builder()
                    .characters(characterIds)
                    .build();

            // when
            when(userService.getCurrentUser(sessionUser)).thenReturn(another);
            when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
            when(partyMapper.isAlreadyJoinedUser(party.getId(), another.getId())).thenReturn(false);
            when(partyMapper.getJoinedMemberSize(party.getId())).thenReturn(2);
            when(characterService.findAllById(characterIds)).thenReturn(characters);
            when(partyMapper.isAlreadyJoinedCharacter(party.getId(), characterIds)).thenReturn(false);

            // then
            PartyResponse response = partyService.joinParty(sessionUser, 1L, request);

            assertAll(
                    () -> assertEquals(party.getId(), response.getId()),
                    () -> assertEquals(party.getName(), response.getName()),
                    () -> verify(partyMapper).isAlreadyJoinedUser(party.getId(), another.getId()),
                    () -> verify(partyMapper).getJoinedMemberSize(party.getId()),
                    () -> verify(characterService).findAllById(characterIds),
                    () -> verify(partyMapper).isAlreadyJoinedCharacter(party.getId(), characterIds)
            );
        }

        @Test
        @DisplayName("이미 가입된 유저의 파티 가입 시도 - 성공")
        void joinParty_AlreadyJoinedUser_Success() {
            // given
            User user = User.builder().email("test@test.com").nickname("tester").build();
            ReflectionTestUtils.setField(user, "id", 1L);
            SessionUser sessionUser = new SessionUser(user);

            Party party = Party.builder()
                    .name("테스트파티")
                    .capacity(4)
                    .user(user)
                    .partyType(PartyType.FRIENDLY)
                    .build();
            ReflectionTestUtils.setField(party, "id", 1L);

            List<Long> characterIds = List.of(1L);
            List<Characters> characters = new ArrayList<>();
            Characters character1 = Characters.builder().user(user).characterName("테스트캐릭터1").build();
            ReflectionTestUtils.setField(character1, "id", 1L);
            characters.add(character1);
            PartyJoinRequest request = new PartyJoinRequest(characterIds);

            // when
            when(userService.getCurrentUser(sessionUser)).thenReturn(user);
            when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
            when(partyMapper.isAlreadyJoinedUser(party.getId(), user.getId())).thenReturn(true);
            when(characterService.findAllById(characterIds)).thenReturn(characters);
            when(partyMapper.isAlreadyJoinedCharacter(party.getId(), characterIds)).thenReturn(false);

            // then
            PartyResponse response = partyService.joinParty(sessionUser, 1L, request);

            assertAll(
                    () -> verify(partyMapper, never()).getJoinedMemberSize(party.getId())
            );
        }

        @Test
        @DisplayName("존재하지 않는 캐릭터로 파티 가입 시도 - 실패")
        void joinParty_NonExistentCharacter_ThrowsException() {
            // given
            User user = User.builder().email("test@test.com").nickname("tester").build();
            ReflectionTestUtils.setField(user, "id", 1L);
            SessionUser sessionUser = new SessionUser(user);

            Party party = Party.builder()
                    .name("테스트파티")
                    .capacity(4)
                    .partyType(PartyType.FRIENDLY)
                    .build();
            ReflectionTestUtils.setField(party, "id", 1L);

            List<Long> characterIds = List.of(1L, 2L);
            List<Characters> characters = new ArrayList<>();
            Characters character1 = Characters.builder().user(user).characterName("테스트캐릭터1").build();
            ReflectionTestUtils.setField(character1, "id", 1L);
            characters.add(character1);
            PartyJoinRequest request = new PartyJoinRequest(characterIds);

            // when
            when(userService.getCurrentUser(sessionUser)).thenReturn(user);
            when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
            when(partyMapper.isAlreadyJoinedUser(party.getId(), user.getId())).thenReturn(false);
            when(characterService.findAllById(characterIds)).thenReturn(characters);

            // then
            assertThrows(RuntimeException.class, () ->
                            partyService.joinParty(sessionUser, 1L, request),
                    "존재하지 않는 캐릭터가 포함되어 있습니다."
            );
        }

        @Test
        @DisplayName("파티 정원 초과로 가입 실패")
        void joinParty_ExceedCapacity_ThrowsException() {
            // given
            User user = User.builder().email("test@test.com").nickname("tester").build();
            ReflectionTestUtils.setField(user, "id", 1L);
            SessionUser sessionUser = new SessionUser(user);

            Party party = Party.builder()
                    .name("테스트파티")
                    .capacity(4)
                    .partyType(PartyType.FRIENDLY)
                    .build();
            ReflectionTestUtils.setField(party, "id", 1L);

            PartyJoinRequest request = new PartyJoinRequest(List.of(1L));

            // when
            when(userService.getCurrentUser(sessionUser)).thenReturn(user);
            when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
            when(partyMapper.isAlreadyJoinedUser(party.getId(), user.getId())).thenReturn(false);
            when(partyMapper.getJoinedMemberSize(party.getId())).thenReturn(4);

            // then
            assertThrows(RuntimeException.class, () ->
                            partyService.joinParty(sessionUser, 1L, request),
                    "공유방 인원 제한이 모두 차 입장할 수 없습니다."
            );
        }

        @Test
        @DisplayName("다른 유저의 캐릭터로 파티 가입 시도 - 실패")
        void joinParty_WithOtherUserCharacter_ThrowsException() {
            // given
            User user = User.builder().email("test@test.com").nickname("tester").build();
            ReflectionTestUtils.setField(user, "id", 1L);

            User otherUser = User.builder().email("other@test.com").nickname("other").build();
            ReflectionTestUtils.setField(otherUser, "id", 2L);

            SessionUser sessionUser = new SessionUser(user);

            Party party = Party.builder()
                    .name("테스트파티")
                    .capacity(4)
                    .partyType(PartyType.FRIENDLY)
                    .build();
            ReflectionTestUtils.setField(party, "id", 1L);

            List<Long> characterIds = List.of(1L);
            List<Characters> characters = new ArrayList<>();
            Characters otherUserCharacter = Characters.builder()
                    .user(otherUser)
                    .characterName("다른유저캐릭터")
                    .build();
            ReflectionTestUtils.setField(otherUserCharacter, "id", 1L);
            characters.add(otherUserCharacter);

            PartyJoinRequest request = new PartyJoinRequest(characterIds);

            // when
            when(userService.getCurrentUser(sessionUser)).thenReturn(user);
            when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
            when(partyMapper.isAlreadyJoinedUser(party.getId(), user.getId())).thenReturn(false);
            when(characterService.findAllById(characterIds)).thenReturn(characters);

            // then
            assertThrows(RuntimeException.class, () ->
                            partyService.joinParty(sessionUser, 1L, request),
                    "본인이 등록한 캐릭터만 공유방에 참여할 수 있습니다."
            );
        }

        @Test
        @DisplayName("이미 가입된 캐릭터로 재가입 시도 - 실패")
        void joinParty_WithAlreadyJoinedCharacter_ThrowsException() {
            // given
            User user = User.builder().email("test@test.com").nickname("tester").build();
            ReflectionTestUtils.setField(user, "id", 1L);
            SessionUser sessionUser = new SessionUser(user);

            Party party = Party.builder()
                    .name("테스트파티")
                    .capacity(4)
                    .partyType(PartyType.FRIENDLY)
                    .build();
            ReflectionTestUtils.setField(party, "id", 1L);

            List<Long> characterIds = List.of(1L);
            List<Characters> characters = new ArrayList<>();
            Characters character = Characters.builder()
                    .user(user)
                    .characterName("테스트캐릭터")
                    .build();
            ReflectionTestUtils.setField(character, "id", 1L);
            characters.add(character);

            PartyJoinRequest request = new PartyJoinRequest(characterIds);

            // when
            when(userService.getCurrentUser(sessionUser)).thenReturn(user);
            when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
            when(partyMapper.isAlreadyJoinedUser(party.getId(), user.getId())).thenReturn(false);
            when(characterService.findAllById(characterIds)).thenReturn(characters);
            when(partyMapper.isAlreadyJoinedCharacter(party.getId(), characterIds)).thenReturn(true);

            // then
            assertThrows(RuntimeException.class, () ->
                            partyService.joinParty(sessionUser, 1L, request),
                    "중복된 캐릭터 참여입니다."
            );
        }
    }
}