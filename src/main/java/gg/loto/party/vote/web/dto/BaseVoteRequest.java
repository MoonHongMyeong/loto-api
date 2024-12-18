package gg.loto.party.vote.web.dto;

import gg.loto.global.entity.BaseEntity;
import gg.loto.global.exception.ErrorCode;
import gg.loto.party.exception.VoteException;
import gg.loto.raid.entity.Difficulty;
import gg.loto.raid.entity.RaidType;

import java.time.LocalDateTime;

public abstract class BaseVoteRequest extends BaseEntity {
    protected void validateRaidRequest(String raidType, String difficulty,
                                       int targetGateNumber, LocalDateTime voteExpiresAt,
                                       LocalDateTime raidDatetime) {
        if (voteExpiresAt.isBefore(raidDatetime)) {
            throw new VoteException(ErrorCode.INVALID_VOTE_EXPIRY_TIME);
        }

        RaidType raidTypeEnum = RaidType.valueOf(raidType);
        Difficulty difficultyEnum = Difficulty.valueOf(difficulty);

        if (!raidTypeEnum.isValidStage(targetGateNumber)) {
            throw new VoteException(ErrorCode.INVALID_RAID_GATE_NUMBER);
        }
        if (!raidTypeEnum.canEnterWithDifficulty(difficultyEnum)) {
            throw new VoteException(ErrorCode.INVALID_RAID_DIFFICULTY);
        }
    }
}
