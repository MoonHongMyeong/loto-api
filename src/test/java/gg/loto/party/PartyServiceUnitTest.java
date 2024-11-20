package gg.loto.party;

import gg.loto.global.auth.dto.SessionUser;
import gg.loto.party.domain.Party;
import gg.loto.party.domain.PartyType;
import gg.loto.party.repository.PartyRepository;
import gg.loto.party.service.PartyService;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PartyServiceUnitTest {

    @InjectMocks
    private PartyService partyService;

    @Mock
    private PartyRepository partyRepository;

    @Mock
    private UserService userService;

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
}