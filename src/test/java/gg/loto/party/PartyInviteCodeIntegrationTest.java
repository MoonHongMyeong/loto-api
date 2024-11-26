package gg.loto.party;

import gg.loto.party.domain.Party;
import gg.loto.party.domain.PartyInviteCodes;
import gg.loto.party.domain.PartyType;
import gg.loto.party.repository.PartyInviteCodesRepository;
import gg.loto.party.repository.PartyRepository;
import gg.loto.party.service.PartyInviteCodeService;
import gg.loto.party.web.dto.PartyResponse;
import gg.loto.user.domain.User;
import gg.loto.user.repository.UserRepository;
import gg.loto.user.web.dto.UserSaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"h2", "local"})
@Transactional
public class PartyInviteCodeIntegrationTest {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private PartyInviteCodeService partyInviteCodeService;

    @Autowired
    private PartyRepository partyRepository;

    @Autowired
    private PartyInviteCodesRepository inviteCodesRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("초대 코드로 파티 조회 - 통합 테스트")
    void getPartyByInviteCode() {
        // given
        UserSaveRequest userRequest = UserSaveRequest.builder()
                .nickname("방장닉네임")
                .password("password1234")
                .email("leader@test.com")
                .build();

        User leader = userRepository.save(userRequest.toEntity(passwordEncoder));

        Party party = partyRepository.save(Party.builder()
                .user(leader)
                .name("테스트 파티")
                .capacity(10)
                .partyType(PartyType.FRIENDLY)
                .build());

        PartyInviteCodes inviteCode = party.generateInviteCode();

        // when
        PartyResponse response = partyInviteCodeService.getPartyByInviteCode(
                inviteCode.getCode().toString()
        );

        // then
        assertThat(response.getId()).isEqualTo(party.getId());
        assertThat(response.getName()).isEqualTo("테스트 파티");
        assertThat(response.getCapacity()).isEqualTo(10);
        assertThat(response.getPartyType()).isEqualTo(PartyType.FRIENDLY.getTypeKor());
    }

    @Test
    @DisplayName("만료된 초대 코드로 파티 조회시 예외 발생")
    void getPartyByInviteCode_Expired() {
        // given
        UserSaveRequest userRequest = UserSaveRequest.builder()
                .nickname("방장닉네임")
                .password("password1234")
                .email("leader@test.com")
                .build();

        User leader = userRepository.save(userRequest.toEntity(passwordEncoder));

        Party party = partyRepository.save(Party.builder()
                .user(leader)
                .name("테스트 파티")
                .capacity(10)
                .partyType(PartyType.FRIENDLY)
                .build());

        PartyInviteCodes inviteCode = PartyInviteCodes.builder()
                .party(party)
                .code(UUID.randomUUID())
                .expiresAt(LocalDateTime.now().minusDays(1))  // 만료된 코드
                .build();
        inviteCodesRepository.save(inviteCode);

        // when & then
        assertThatThrownBy(() ->
                partyInviteCodeService.getPartyByInviteCode(inviteCode.getCode().toString())
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("유효기간이 만료된 코드입니다.");
    }
}
