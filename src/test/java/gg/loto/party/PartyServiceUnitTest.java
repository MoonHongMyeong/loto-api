package gg.loto.party;

import gg.loto.global.auth.dto.SessionUser;
import gg.loto.party.domain.Party;
import gg.loto.party.domain.PartyType;
import gg.loto.party.repository.PartyRepository;
import gg.loto.party.service.PartyService;
import gg.loto.party.web.dto.PartyResponse;
import gg.loto.party.web.dto.PartySaveRequest;
import gg.loto.user.domain.User;
import gg.loto.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    @DisplayName("공유방 생성이 정상적으로 동작한다")
    void createParty() {
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