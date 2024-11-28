package gg.loto.party.vote;

import gg.loto.character.domain.Characters;
import gg.loto.party.vote.domain.PartyRaidVote;
import gg.loto.party.vote.domain.PartyRaidVoteParticipant;
import gg.loto.party.vote.domain.VoteStatus;
import gg.loto.raid.entity.Difficulty;
import gg.loto.raid.entity.RaidType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PartyRaidVoteUnitTest {
    @Test
    @DisplayName("투표 생성 시 기본 상태값이 정상적으로 설정된다")
    void create_InitialStateIsCorrect() {
        // given
        LocalDateTime raidDateTime = LocalDateTime.now().plusDays(1);
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);
        
        // when
        PartyRaidVote vote = PartyRaidVote.builder()
                .raidType(RaidType.VALTAN)
                .difficulty(Difficulty.NORMAL)
                .targetGateNumber(1)
                .raidDatetime(raidDateTime)
                .voteExpiresAt(expiresAt)
                .voteStatus(VoteStatus.IN_PROGRESS)
                .build();

        // then
        assertThat(vote.getVoteStatus()).isEqualTo(VoteStatus.IN_PROGRESS);
        assertThat(vote.getParticipants()).isEmpty();
    }

    @Test
    @DisplayName("레이드 타입의 제한 인원을 초과하여 참여할 수 없다")
    void join_ThrowsException_WhenExceedingPartySize() {
        // given
        PartyRaidVote vote = createVote(RaidType.ECHIDNA); // 4인 레이드
        
        // 4명의 참여자 추가
        for (int i = 0; i < 8; i++) {
            vote.join(createParticipant(vote));
        }

        // when & then
        assertThatThrownBy(() -> vote.join(createParticipant(vote)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("제한 인원이 초과되었습니다.");
    }

    @Test
    @DisplayName("모든 인원이 참여하면 투표가 완료 상태가 된다")
    void join_CompletesVote_WhenPartyIsFull() {
        // given
        PartyRaidVote vote = createVote(RaidType.BEHEMOTH); // 16인 레이드

        // when
        for (int i = 0; i < 16; i++) {
            vote.join(createParticipant(vote));
        }

        // then
        assertThat(vote.getVoteStatus()).isEqualTo(VoteStatus.COMPLETE);
    }

    @Test
    @DisplayName("참여 취소가 정상적으로 동작한다")
    void leave_RemovesParticipant() {
        // given
        PartyRaidVote vote = createVote(RaidType.VALTAN);
        PartyRaidVoteParticipant participant = createParticipant(vote);
        vote.join(participant);

        // when
        vote.leave(participant);

        // then
        assertThat(vote.hasParticipant(participant)).isFalse();
    }

    @Test
    @DisplayName("투표 취소 시 상태가 변경된다")
    void cancel_ChangesStatusToCanceled() {
        // given
        PartyRaidVote vote = createVote(RaidType.VALTAN);

        // when
        vote.cancel();

        // then
        assertThat(vote.getVoteStatus()).isEqualTo(VoteStatus.CANCEL);
    }

    private PartyRaidVote createVote(RaidType raidType) {
        return PartyRaidVote.builder()
                .raidType(raidType)
                .difficulty(Difficulty.NORMAL)
                .targetGateNumber(1)
                .raidDatetime(LocalDateTime.now().plusDays(1))
                .voteExpiresAt(LocalDateTime.now().plusHours(24))
                .voteStatus(VoteStatus.IN_PROGRESS)
                .build();
    }

    private PartyRaidVoteParticipant createParticipant(PartyRaidVote vote) {
        return PartyRaidVoteParticipant.builder()
                .vote(vote)
                .character(new Characters())
                .build();
    }
}