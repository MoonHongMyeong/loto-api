package gg.loto.party;

import gg.loto.party.domain.Party;
import gg.loto.party.domain.PartyInviteCodes;
import gg.loto.party.domain.PartyType;
import gg.loto.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PartyInviteCodeUnitTest {
    @Test
    @DisplayName("초대코드 생성 시 유효기간은 24시간으로 설정된다")
    void generateInviteCode_ExpiresIn24Hours() {
        // given
        Party party = createTestParty();
        LocalDateTime now = LocalDateTime.now();

        // when
        PartyInviteCodes inviteCode = party.generateInviteCode();

        // then
        assertThat(inviteCode.getParty()).isEqualTo(party);
        assertThat(inviteCode.getCode()).isNotNull();
        assertThat(inviteCode.getExpiresAt())
                .isBetween(
                        now.plusHours(23).plusMinutes(59),
                        now.plusHours(24).plusMinutes(1)
                );
    }

    @Test
    @DisplayName("생성된 초대코드는 파티의 초대코드 목록에 추가된다")
    void generateInviteCode_AddedToPartyInviteCodes() {
        // given
        Party party = createTestParty();

        // when
        PartyInviteCodes inviteCode = party.generateInviteCode();

        // then
        assertThat(party.getInviteCodes())
                .hasSize(1)
                .contains(inviteCode);
    }

    @Test
    @DisplayName("초대코드의 만료 여부를 정확히 판단한다")
    void inviteCode_ExpirationCheck() {
        // given
        Party party = createTestParty();
        PartyInviteCodes inviteCode = party.generateInviteCode();

        // when & then
        assertThat(inviteCode.isExpired()).isFalse();

        // 만료된 코드 테스트
        PartyInviteCodes expiredCode = PartyInviteCodes.builder()
                .party(party)
                .code(UUID.randomUUID())
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .build();

        assertThat(expiredCode.isExpired()).isTrue();
    }

    private Party createTestParty() {
        return Party.builder()
                .user(createTestUser())
                .name("테스트 파티")
                .capacity(10)
                .partyType(PartyType.FRIENDLY)
                .build();
    }

    private User createTestUser() {
        return User.builder()
                .email("test@test.com")
                .password("password")
                .nickname("tester")
                .build();
    }
}
