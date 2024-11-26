package gg.loto.party;

import gg.loto.character.domain.Characters;
import gg.loto.character.service.CharactersService;
import gg.loto.global.auth.dto.SessionUser;
import gg.loto.party.domain.Party;
import gg.loto.party.domain.PartyType;
import gg.loto.party.mapper.PartyMapper;
import gg.loto.party.repository.PartyMemberRepository;
import gg.loto.party.repository.PartyRepository;
import gg.loto.party.service.PartyService;
import gg.loto.party.web.dto.*;
import gg.loto.user.domain.User;
import gg.loto.user.service.UserFindDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
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
    private UserFindDao userFindDao;

    @Mock
    private CharactersService characterService;

    @Mock
    private PartyMapper partyMapper;

    @Mock
    private PartyMemberRepository partyMemberRepository;

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

            given(userFindDao.getCurrentUser(any())).willReturn(user);
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

            given(userFindDao.getCurrentUser(any())).willReturn(user);
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

            given(userFindDao.getCurrentUser(sessionUser)).willReturn(user);
            given(partyRepository.findById(partyId)).willReturn(Optional.of(party));

            // when
            PartyResponse response = partyService.updateParty(sessionUser, partyId, updateRequest);

            // then
            assertThat(response.getName()).isEqualTo(updateRequest.getName());
            assertThat(response.getCapacity()).isEqualTo(updateRequest.getCapacity());
            assertThat(response.getPartyType()).isEqualTo(PartyType.FRIENDLY.getTypeKor());

            verify(userFindDao).getCurrentUser(sessionUser);
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

            given(userFindDao.getCurrentUser(sessionUser)).willReturn(user);
            given(partyRepository.findById(invalidPartyId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    partyService.updateParty(sessionUser, invalidPartyId, updateRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("잘못된 공유방입니다.");
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

            given(userFindDao.getCurrentUser(sessionUser)).willReturn(otherUser);
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

            given(userFindDao.getCurrentUser(sessionUser)).willReturn(currentLeader);
            given(partyRepository.findById(partyId)).willReturn(Optional.of(party));
            given(userFindDao.findById(newLeader.getId())).willReturn(newLeader);

            // when
            PartyResponse response = partyService.transferLeadership(sessionUser, partyId, newLeader.getId());

            // then
            assertThat(response.getNickname()).isEqualTo(newLeader.getNickname());
            verify(userFindDao).getCurrentUser(sessionUser);
            verify(partyRepository).findById(partyId);
            verify(userFindDao).findById(newLeader.getId());
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

            given(userFindDao.getCurrentUser(sessionUser)).willReturn(currentLeader);
            given(partyRepository.findById(invalidPartyId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    partyService.transferLeadership(sessionUser, invalidPartyId, newLeaderId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("잘못된 공유방입니다.");
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

            given(userFindDao.getCurrentUser(sessionUser)).willReturn(otherUser);
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

            given(userFindDao.getCurrentUser(sessionUser)).willReturn(currentLeader);
            given(partyRepository.findById(partyId)).willReturn(Optional.of(party));
            given(userFindDao.findById(invalidUserId))
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

            PartyMemberRequest request = PartyMemberRequest.builder()
                    .characters(characterIds)
                    .build();

            // when
            when(userFindDao.getCurrentUser(sessionUser)).thenReturn(user);
            when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
            when(partyMapper.isPartyMember(party.getId(), user.getId())).thenReturn(false);
            when(partyMapper.getJoinedMemberSize(party.getId())).thenReturn(2);
            when(characterService.findAllById(request.getCharacters())).thenReturn(characters);
            when(partyMapper.isAlreadyJoinedCharacter(party.getId(), request.getCharacters())).thenReturn(false);

            // then
            PartyResponse response = partyService.joinParty(sessionUser, 1L, request);

            assertAll(
                    () -> assertEquals(party.getId(), response.getId()),
                    () -> assertEquals(party.getName(), response.getName()),
                    () -> verify(partyMapper).isPartyMember(party.getId(), user.getId()),
                    () -> verify(partyMapper).getJoinedMemberSize(party.getId()),
                    () -> verify(characterService).findAllById(request.getCharacters()),
                    () -> verify(partyMapper).isAlreadyJoinedCharacter(party.getId(), request.getCharacters())
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

            PartyMemberRequest request = PartyMemberRequest.builder()
                    .characters(characterIds)
                    .build();

            // when
            when(userFindDao.getCurrentUser(sessionUser)).thenReturn(another);
            when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
            when(partyMapper.isPartyMember(party.getId(), another.getId())).thenReturn(false);
            when(partyMapper.getJoinedMemberSize(party.getId())).thenReturn(2);
            when(characterService.findAllById(request.getCharacters())).thenReturn(characters);
            when(partyMapper.isAlreadyJoinedCharacter(party.getId(), request.getCharacters())).thenReturn(false);

            // then
            PartyResponse response = partyService.joinParty(sessionUser, 1L, request);

            assertAll(
                    () -> assertEquals(party.getId(), response.getId()),
                    () -> assertEquals(party.getName(), response.getName()),
                    () -> verify(partyMapper).isPartyMember(party.getId(), another.getId()),
                    () -> verify(partyMapper).getJoinedMemberSize(party.getId()),
                    () -> verify(characterService).findAllById(request.getCharacters()),
                    () -> verify(partyMapper).isAlreadyJoinedCharacter(party.getId(), request.getCharacters())
            );
        }

        @Test
        @DisplayName("이미 가입된 유저의 공유방 가입 시도 - 성공")
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
            PartyMemberRequest request = new PartyMemberRequest(characterIds);

            // when
            when(userFindDao.getCurrentUser(sessionUser)).thenReturn(user);
            when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
            when(partyMapper.isPartyMember(party.getId(), user.getId())).thenReturn(true);
            when(characterService.findAllById(request.getCharacters())).thenReturn(characters);
            when(partyMapper.isAlreadyJoinedCharacter(party.getId(), request.getCharacters())).thenReturn(false);

            // then
            PartyResponse response = partyService.joinParty(sessionUser, 1L, request);

            assertAll(
                    () -> verify(partyMapper, never()).getJoinedMemberSize(party.getId())
            );
        }

        @Test
        @DisplayName("존재하지 않는 캐릭터로 공유방 가입 시도 - 실패")
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
            PartyMemberRequest request = new PartyMemberRequest(characterIds);

            // when
            when(userFindDao.getCurrentUser(sessionUser)).thenReturn(user);
            when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
            when(partyMapper.isPartyMember(party.getId(), user.getId())).thenReturn(false);
            when(characterService.findAllById(request.getCharacters())).thenReturn(characters);

            // then
            assertThrows(RuntimeException.class, () ->
                            partyService.joinParty(sessionUser, 1L, request),
                    "존재하지 않는 캐릭터가 포함되어 있습니다."
            );
        }

        @Test
        @DisplayName("공유방 정원 초과로 가입 실패")
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

            PartyMemberRequest request = new PartyMemberRequest(List.of(1L));

            // when
            when(userFindDao.getCurrentUser(sessionUser)).thenReturn(user);
            when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
            when(partyMapper.isPartyMember(party.getId(), user.getId())).thenReturn(false);
            when(partyMapper.getJoinedMemberSize(party.getId())).thenReturn(4);

            // then
            assertThrows(RuntimeException.class, () ->
                            partyService.joinParty(sessionUser, 1L, request),
                    "공유방 인원 제한이 모두 차 입장할 수 없습니다."
            );
        }

        @Test
        @DisplayName("다른 유저의 캐릭터로 공유방 가입 시도 - 실패")
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

            PartyMemberRequest request = new PartyMemberRequest(characterIds);

            // when
            when(userFindDao.getCurrentUser(sessionUser)).thenReturn(user);
            when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
            when(partyMapper.isPartyMember(party.getId(), user.getId())).thenReturn(false);
            when(characterService.findAllById(request.getCharacters())).thenReturn(characters);

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

            PartyMemberRequest request = new PartyMemberRequest(characterIds);

            // when
            when(userFindDao.getCurrentUser(sessionUser)).thenReturn(user);
            when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
            when(partyMapper.isPartyMember(party.getId(), user.getId())).thenReturn(false);
            when(characterService.findAllById(request.getCharacters())).thenReturn(characters);
            when(partyMapper.isAlreadyJoinedCharacter(party.getId(), request.getCharacters())).thenReturn(true);

            // then
            assertThrows(RuntimeException.class, () ->
                            partyService.joinParty(sessionUser, 1L, request),
                    "중복된 캐릭터 참여입니다."
            );
        }
    }

    @Nested
    @DisplayName("공유방 탈퇴 테스트")
    class LeaveParty {
        @Test
        @DisplayName("공유방 탈퇴 - 정상 케이스")
        void leaveParty_Success() {
            // given
            User user = User.builder()
                    .email("test@test.com")
                    .nickname("tester")
                    .build();
            ReflectionTestUtils.setField(user, "id", 1L);

            Party party = Party.builder()
                    .user(user)
                    .name("테스트파티")
                    .capacity(4)
                    .partyType(PartyType.FRIENDLY)
                    .build();
            ReflectionTestUtils.setField(party, "id", 1L);

            User otherUser = User.builder()
                    .email("test2@test.com")
                    .nickname("tester2")
                    .build();
            ReflectionTestUtils.setField(otherUser, "id", 2L);
            SessionUser sessionUser = new SessionUser(user);

            List<Characters> characters = new ArrayList<>();
            Characters character1 = Characters.builder()
                    .user(otherUser)
                    .characterName("테스트캐릭터1")
                    .build();
            Characters character2 = Characters.builder()
                    .user(otherUser)
                    .characterName("테스트캐릭터2")
                    .build();
            ReflectionTestUtils.setField(character1, "id", 1L);
            ReflectionTestUtils.setField(character2, "id", 2L);
            characters.add(character1);
            characters.add(character2);

            List<Long> characterIds = List.of(1L, 2L);
            PartyMemberRequest request = new PartyMemberRequest(characterIds);

            when(userFindDao.getCurrentUser(sessionUser)).thenReturn(otherUser);
            when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
            when(partyMapper.isPartyMember(1L, 2L)).thenReturn(true);
            when(characterService.findAllById(request.getCharacters())).thenReturn(characters);
            when(partyMapper.isAlreadyJoinedCharacter(1L, request.getCharacters())).thenReturn(true);

            // when
            assertDoesNotThrow(() -> partyService.leaveParty(sessionUser, 1L, request));

            // then
            verify(partyMemberRepository).deleteByPartyIdAndCharacterIdIn(1L, request.getCharacters());
        }

        @Test
        @DisplayName("공유방 탈퇴 - 방장이 일부 캐릭터만 탈퇴")
        void leaveParty_PartyLeaderPartialLeave() {
            // given
            User user = User.builder()
                    .email("test@test.com")
                    .nickname("tester")
                    .build();
            ReflectionTestUtils.setField(user, "id", 1L);
            SessionUser sessionUser = new SessionUser(user);

            Party party = Party.builder()
                    .user(user)
                    .name("테스트파티")
                    .capacity(4)
                    .partyType(PartyType.FRIENDLY)
                    .build();
            ReflectionTestUtils.setField(party, "id", 1L);

            List<Characters> characters = new ArrayList<>();
            Characters character1 = Characters.builder()
                    .user(user)
                    .characterName("테스트캐릭터1")
                    .build();
            Characters character2 = Characters.builder()
                    .user(user)
                    .characterName("테스트캐릭터2")
                    .build();
            ReflectionTestUtils.setField(character1, "id", 1L);
            ReflectionTestUtils.setField(character2, "id", 2L);
            characters.add(character1);
            characters.add(character2);

            List<Long> characterIds = List.of(1L, 2L, 1L);
            PartyMemberRequest request = new PartyMemberRequest(characterIds);

            when(userFindDao.getCurrentUser(sessionUser)).thenReturn(user);
            when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
            when(characterService.findAllById(request.getCharacters())).thenReturn(characters);
            when(partyMapper.isPartyMember(1L, 1L)).thenReturn(true);
            when(partyMapper.isAlreadyJoinedCharacter(1L, request.getCharacters())).thenReturn(true);
            when(partyMapper.getPartyLeaderCharactersSize(1L)).thenReturn(3); // 전체 3개 중 2개 탈퇴

            // when
            assertDoesNotThrow(() -> partyService.leaveParty(sessionUser, 1L, request));

            // then
            verify(partyMemberRepository).deleteByPartyIdAndCharacterIdIn(1L, request.getCharacters());
        }

        @Test
        @DisplayName("공유방 탈퇴 - 중복된 캐릭터 ID가 요청되어도 정상 처리")
        void leaveParty_WithDuplicateCharacterIds() {
            // given
            User user = User.builder()
                    .email("test@test.com")
                    .nickname("tester")
                    .build();
            ReflectionTestUtils.setField(user, "id", 1L);

            User other = User.builder()
                    .email("test@test.com")
                    .nickname("tester")
                    .build();
            ReflectionTestUtils.setField(other, "id", 2L);
            SessionUser sessionUser = new SessionUser(other);

            Party party = Party.builder()
                    .user(user)
                    .name("테스트파티")
                    .capacity(4)
                    .partyType(PartyType.FRIENDLY)
                    .build();
            ReflectionTestUtils.setField(party, "id", 1L);

            List<Long> duplicateIds = Arrays.asList(1L, 1L, 2L);
            PartyMemberRequest request = new PartyMemberRequest(duplicateIds);

            Characters character1 = Characters.builder()
                    .user(other)
                    .characterName("테스트캐릭터1")
                    .build();
            Characters character2 = Characters.builder()
                    .user(other)
                    .characterName("테스트캐릭터2")
                    .build();
            Characters character3 = Characters.builder()
                    .user(other)
                    .characterName("남아있을_캐릭터")
                    .build();
            ReflectionTestUtils.setField(character1, "id", 1L);
            ReflectionTestUtils.setField(character2, "id", 2L);
            ReflectionTestUtils.setField(character3, "id", 3L);

            when(userFindDao.getCurrentUser(sessionUser)).thenReturn(other);
            when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
            when(partyMapper.isPartyMember(1L, other.getId())).thenReturn(true);
            when(characterService.findAllById(any(Set.class))).thenReturn(List.of(character1, character2));
            when(partyMapper.isAlreadyJoinedCharacter(eq(1L), any(Set.class))).thenReturn(true);

            // when
            assertDoesNotThrow(() -> partyService.leaveParty(sessionUser, 1L, request));

            // then
            verify(characterService).findAllById(any(Set.class));
            verify(partyMapper).isAlreadyJoinedCharacter(eq(1L), any(Set.class));
            verify(partyMemberRepository).deleteByPartyIdAndCharacterIdIn(eq(1L), any(Set.class));
        }

        @Test
        @DisplayName("공유방 탈퇴 - 방장이 전체 캐릭터 탈퇴 시도")
        void leaveParty_PartyLeaderAllCharactersLeave() {
            // given
            User user = User.builder()
                    .email("test@test.com")
                    .nickname("tester")
                    .build();
            ReflectionTestUtils.setField(user, "id", 1L);
            SessionUser sessionUser = new SessionUser(user);

            Party party = Party.builder()
                    .user(user)
                    .name("테스트파티")
                    .capacity(4)
                    .partyType(PartyType.FRIENDLY)
                    .build();
            ReflectionTestUtils.setField(party, "id", 1L);

            List<Characters> characters = new ArrayList<>();
            Characters character1 = Characters.builder()
                    .user(user)
                    .characterName("테스트캐릭터1")
                    .build();
            Characters character2 = Characters.builder()
                    .user(user)
                    .characterName("테스트캐릭터2")
                    .build();
            ReflectionTestUtils.setField(character1, "id", 1L);
            ReflectionTestUtils.setField(character2, "id", 2L);
            characters.add(character1);
            characters.add(character2);

            List<Long> characterIds = List.of(1L, 2L);
            PartyMemberRequest request = new PartyMemberRequest(characterIds);

            when(userFindDao.getCurrentUser(sessionUser)).thenReturn(user);
            when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
            when(characterService.findAllById(request.getCharacters())).thenReturn(characters);
            when(partyMapper.isPartyMember(1L, 1L)).thenReturn(true);
            when(partyMapper.isAlreadyJoinedCharacter(1L, request.getCharacters())).thenReturn(true);
            when(partyMapper.getPartyLeaderCharactersSize(1L)).thenReturn(2); // 전체 캐릭터 수와 탈퇴 요청 캐릭터 수가 동일

            // when & then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> partyService.leaveParty(sessionUser, 1L, request)
            );
            assertEquals("방장은 최소 한 캐릭터는 소유해야 합니다.\n공유방을 떠나려면 다른 사용자에게 방장을 위임해주세요.", exception.getMessage());

            verify(partyMemberRepository, never()).deleteByPartyIdAndCharacterIdIn(anyLong(), anySet());
        }

        @Test
        @DisplayName("공유방 탈퇴 - 참여하지 않은 유저")
        void leaveParty_NotJoinedUser() {
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
                    .partyType(PartyType.FRIENDLY)
                    .build();
            ReflectionTestUtils.setField(party, "id", 1L);

            PartyMemberRequest request = new PartyMemberRequest(List.of(1L));

            when(userFindDao.getCurrentUser(sessionUser)).thenReturn(user);
            when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
            when(partyMapper.isPartyMember(1L, 1L)).thenReturn(false);

            // when & then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> partyService.leaveParty(sessionUser, 1L, request)
            );
            assertEquals("참여한 공유방이 아닙니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("공유방 강제 퇴장 테스트")
    class KickMemberTest {
        @Test
        @DisplayName("방장이 일반 멤버를 강제 퇴장시키면 성공한다")
        void kickMember_Success() {
                // given
                User leader = User.builder()
                        .email("leader@test.com")
                        .nickname("방장")
                        .build();
                ReflectionTestUtils.setField(leader, "id", 1L);
                SessionUser sessionUser = new SessionUser(leader);

                User member = User.builder()
                        .email("member@test.com")
                        .nickname("멤버")
                        .build();
                ReflectionTestUtils.setField(member, "id", 2L);

                Party party = Party.builder()
                        .name("테스트파티")
                        .capacity(4)
                        .user(leader)
                        .partyType(PartyType.FRIENDLY)
                        .build();
                ReflectionTestUtils.setField(party, "id", 1L);

                when(userFindDao.getCurrentUser(sessionUser)).thenReturn(leader);
                when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
                when(userFindDao.findById(2L)).thenReturn(member);
                when(partyMapper.isPartyMember(1L, 2L)).thenReturn(true);

                // when
                assertDoesNotThrow(() -> 
                partyService.kickMember(sessionUser, 1L, 2L));

                // then
                verify(partyMemberRepository).deleteByPartyIdAndUserId(1L, 2L);
        }

        @Test
        @DisplayName("방장이 아닌 사용자가 강제 퇴장을 시도하면 실패한다")
        void kickMember_NotLeader_ThrowsException() {
                // given
                User leader = User.builder()
                        .email("leader@test.com")
                        .nickname("방장")
                        .build();
                ReflectionTestUtils.setField(leader, "id", 1L);

                User member = User.builder()
                        .email("member@test.com")
                        .nickname("멤버")
                        .build();
                ReflectionTestUtils.setField(member, "id", 2L);
                SessionUser sessionUser = new SessionUser(member);

                Party party = Party.builder()
                        .name("테스트파티")
                        .capacity(4)
                        .user(leader)
                        .partyType(PartyType.FRIENDLY)
                        .build();
                ReflectionTestUtils.setField(party, "id", 1L);

                when(userFindDao.getCurrentUser(sessionUser)).thenReturn(member);
                when(partyRepository.findById(1L)).thenReturn(Optional.of(party));

                // when & then
                assertThrows(RuntimeException.class, () ->
                partyService.kickMember(sessionUser, 1L, 3L),
                "권한이 없는 요청입니다.");
        }

        @Test
        @DisplayName("존재하지 않는 공유방에서 강제 퇴장을 시도하면 실패한다")
        void kickMember_PartyNotFound_ThrowsException() {
                // given
                User leader = User.builder()
                        .email("leader@test.com")
                        .nickname("방장")
                        .build();
                ReflectionTestUtils.setField(leader, "id", 1L);
                SessionUser sessionUser = new SessionUser(leader);

                when(partyRepository.findById(1L)).thenReturn(Optional.empty());

                // when & then
                assertThrows(IllegalArgumentException.class, () ->
                partyService.kickMember(sessionUser, 1L, 2L),
                "잘못된 공유방입니다.");
        }

        @Test
        @DisplayName("공유방에 속하지 않은 멤버를 강제 퇴장시키려 하면 실패한다")
        void kickMember_MemberNotInParty_ThrowsException() {
                // given
                User leader = User.builder()
                        .email("leader@test.com")
                        .nickname("방장")
                        .build();
                ReflectionTestUtils.setField(leader, "id", 1L);
                SessionUser sessionUser = new SessionUser(leader);

                User nonMember = User.builder()
                        .email("non-member@test.com")
                        .nickname("비멤버")
                        .build();
                ReflectionTestUtils.setField(nonMember, "id", 2L);

                Party party = Party.builder()
                        .name("테스트파티")
                        .capacity(4)
                        .user(leader)
                        .partyType(PartyType.FRIENDLY)
                        .build();
                ReflectionTestUtils.setField(party, "id", 1L);

                when(userFindDao.getCurrentUser(sessionUser)).thenReturn(leader);
                when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
                when(userFindDao.findById(2L)).thenReturn(nonMember);
                when(partyMapper.isPartyMember(1L, 2L)).thenReturn(false);

                // when & then
                assertThrows(IllegalArgumentException.class, () ->
                partyService.kickMember(sessionUser, 1L, 2L),
                "해당 유저는 공유방에 속해있지 않습니다.");
        }

        @Test
        @DisplayName("방장을 강제 퇴장시키려 하면 실패한다")
        void kickMember_KickLeader_ThrowsException() {
                // given
                User leader = User.builder()
                        .email("leader@test.com")
                        .nickname("방장")
                        .build();
                ReflectionTestUtils.setField(leader, "id", 1L);
                SessionUser sessionUser = new SessionUser(leader);

                Party party = Party.builder()
                        .name("테스트파티")
                        .capacity(4)
                        .user(leader)
                        .partyType(PartyType.FRIENDLY)
                        .build();
                ReflectionTestUtils.setField(party, "id", 1L);

                when(userFindDao.getCurrentUser(sessionUser)).thenReturn(leader);
                when(partyRepository.findById(1L)).thenReturn(Optional.of(party));

                // when & then
                assertThrows(IllegalArgumentException.class, () ->
                partyService.kickMember(sessionUser, 1L, 1L),
                "방장을 강제 퇴장시킬 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("공유방 삭제 테스트")
    class RemoveParty {
        @Test
        @DisplayName("공유방 삭제 성공")
        void removeParty_Success(){
            // given
            User leader = User.builder()
                    .email("leader@test.com")
                    .nickname("방장")
                    .build();
            ReflectionTestUtils.setField(leader, "id", 1L);
            SessionUser sessionUser = new SessionUser(leader);

            Party party = Party.builder()
                    .name("테스트파티")
                    .capacity(4)
                    .user(leader)
                    .partyType(PartyType.FRIENDLY)
                    .build();
            ReflectionTestUtils.setField(party, "id", 1L);

            when(userFindDao.getCurrentUser(sessionUser)).thenReturn(leader);
            when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
            when(partyMapper.getJoinedMemberSize(1L)).thenReturn(1);// 방장 혼자 존재

            //when
            assertDoesNotThrow(() -> partyService.removeParty(sessionUser, party.getId()));

            // then
            verify(partyRepository).delete(party);
        }

        @Test
        @DisplayName("공유방 삭제 실패 - 방장이 아닌 경우")
        void removeParty_fail_notLeader(){
            // given
            User leader = User.builder()
                    .email("leader@test.com")
                    .nickname("방장")
                    .build();
            ReflectionTestUtils.setField(leader, "id", 1L);

            User member = User.builder()
                    .email("member@test.com")
                    .nickname("멤버")
                    .build();
            ReflectionTestUtils.setField(member, "id", 2L);
            SessionUser sessionUser = new SessionUser(member);

            Party party = Party.builder()
                    .name("테스트파티")
                    .capacity(4)
                    .user(leader)
                    .partyType(PartyType.FRIENDLY)
                    .build();
            ReflectionTestUtils.setField(party, "id", 1L);

            when(userFindDao.getCurrentUser(sessionUser)).thenReturn(member);
            when(partyRepository.findById(1L)).thenReturn(Optional.of(party));

            // when & then
            assertThrows(RuntimeException.class, () ->
                            partyService.removeParty(sessionUser, party.getId()),
                    "권한이 없는 요청입니다.");

            verify(partyRepository, never()).delete(any(Party.class));
        }

        @Test
        @DisplayName("공유방 삭제 실패 - 공유방에 다른 멤버가 있는 경우")
        void removeParty_fail_existAnotherMember(){
            // given
            User leader = User.builder()
                    .email("leader@test.com")
                    .nickname("방장")
                    .build();
            ReflectionTestUtils.setField(leader, "id", 1L);
            SessionUser sessionUser = new SessionUser(leader);

            Party party = Party.builder()
                    .name("테스트파티")
                    .capacity(4)
                    .user(leader)
                    .partyType(PartyType.FRIENDLY)
                    .build();
            ReflectionTestUtils.setField(party, "id", 1L);

            when(userFindDao.getCurrentUser(sessionUser)).thenReturn(leader);
            when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
            when(partyMapper.getJoinedMemberSize(1L)).thenReturn(2); // 방장 포함 2명 이상

            // when & then
            assertThrows(RuntimeException.class, () ->
                            partyService.removeParty(sessionUser, party.getId()),
                    "공유방에 다른 사용자가 있으면 삭제가 불가능합니다.");

            verify(partyRepository, never()).delete(any(Party.class));
        }

        @Test
        @DisplayName("공유방 삭제 실패 - 존재하지 않는 공유방")
        void removeParty_fail_partyNotFound() {
            // given
            User leader = User.builder()
                    .email("leader@test.com")
                    .nickname("방장")
                    .build();
            ReflectionTestUtils.setField(leader, "id", 1L);
            SessionUser sessionUser = new SessionUser(leader);

            when(userFindDao.getCurrentUser(sessionUser)).thenReturn(leader);
            when(partyRepository.findById(1L)).thenReturn(Optional.empty());

            // when & then
            assertThrows(IllegalArgumentException.class, () ->
                            partyService.removeParty(sessionUser, 1L),
                    "잘못된 공유방입니다.");

            verify(partyRepository, never()).delete(any(Party.class));
        }
    }
}