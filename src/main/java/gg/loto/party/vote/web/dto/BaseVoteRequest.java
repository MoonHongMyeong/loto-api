package gg.loto.party.vote.web.dto;

import gg.loto.raid.entity.Difficulty;
import gg.loto.raid.entity.RaidType;

import java.time.LocalDateTime;

public abstract class BaseVoteRequest {
    protected void validateRaidRequest(String raidType, String difficulty,
                                       int targetGateNumber, LocalDateTime voteExpiresAt,
                                       LocalDateTime raidDatetime) {
        if (voteExpiresAt.isBefore(raidDatetime)) {
            throw new IllegalArgumentException("투표 마감 시간은 레이드 시작 시간 이전이어야 합니다.");
        }

        RaidType raidTypeEnum = RaidType.valueOf(raidType);
        Difficulty difficultyEnum = Difficulty.valueOf(difficulty);

        if (!raidTypeEnum.isValidStage(targetGateNumber)) {
            throw new IllegalArgumentException("유효하지 않은 관문 번호입니다.");
        }
        if (!raidTypeEnum.canEnterWithDifficulty(difficultyEnum)) {
            throw new IllegalArgumentException("유효하지 않은 난이도입니다.");
        }
    }
}
