package gg.loto.raid.entity;

import gg.loto.global.exception.ErrorCode;
import gg.loto.raid.exception.RaidException;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum RaidType {
    VALTAN("발탄", 2, 8),
    VYKAS("비아키스", 2, 8),
    KOUKUSATON("쿠크세이튼", 3, 4),
    ABRELSHUD("아브렐슈드", 4, 8),
    ILLIAKKAN("일리아칸", 3, 8),
    KAMEN("카멘", 4, 8),
    KAYANGEL("카양겔", 3, 4),
    IVORYTOWER("상아탑", 3, 4),
    ECHIDNA("에키드나", 2, 8),
    BEHEMOTH("베히모스", 2, 16),
    KAZEROTH_STAGE1_EGIR("에기르", 2, 8),
    KAZEROTH_STAGE2_ABRELSHUD("아브렐슈드2막", 2, 8);

    private String bossNameKor;
    private int stageCount;
    private int requiredPartySize;

    RaidType(String bossNameKor, int stageCount, int requiredPartySize) {
        this.bossNameKor = bossNameKor;
        this.stageCount = stageCount;
        this.requiredPartySize = requiredPartySize;
    }

    public int getRequiredItemLevel(Difficulty difficulty){
        return switch (this){
            case VALTAN -> switch(difficulty){
                case NORMAL -> 1415;
                case HARD, HELL -> 1445;
                default -> throw new RaidException(ErrorCode.UNSUPPORTED_RAID_DIFFICULTY);
            };
            case VYKAS -> switch (difficulty){
                case NORMAL -> 1430;
                case HARD, HELL -> 1460;
                default -> throw new RaidException(ErrorCode.UNSUPPORTED_RAID_DIFFICULTY);
            };
            case KOUKUSATON -> switch (difficulty){
                case NORMAL, HELL -> 1475;
                default -> throw new RaidException(ErrorCode.UNSUPPORTED_RAID_DIFFICULTY);
            };
            case ABRELSHUD -> switch (difficulty){
                case NORMAL -> 1490;
                case HARD -> 1540;
                case HELL -> 1560;
                default -> throw new RaidException(ErrorCode.UNSUPPORTED_RAID_DIFFICULTY);
            };
            case ILLIAKKAN -> switch (difficulty){
                case NORMAL -> 1580;
                case HARD -> 1600;
                default -> throw new RaidException(ErrorCode.UNSUPPORTED_RAID_DIFFICULTY);
            };
            case KAMEN -> switch(difficulty){
                case NORMAL -> 1610;
                case HARD -> 1630;
                default -> throw new RaidException(ErrorCode.UNSUPPORTED_RAID_DIFFICULTY);
            };
            case KAYANGEL -> switch (difficulty){
                case NORMAL -> 1540;
                case HARD -> 1580;
                default -> throw new RaidException(ErrorCode.UNSUPPORTED_RAID_DIFFICULTY);
            };
            case IVORYTOWER -> switch (difficulty){
                case NORMAL -> 1600;
                case HARD -> 1620;
                default -> throw new RaidException(ErrorCode.UNSUPPORTED_RAID_DIFFICULTY);
            };
            case ECHIDNA -> switch (difficulty){
                case NORMAL -> 1620;
                case HARD -> 1640;
                default -> throw new RaidException(ErrorCode.UNSUPPORTED_RAID_DIFFICULTY);
            };
            case BEHEMOTH -> switch (difficulty){
                case NORMAL -> 1640;
                default -> throw new RaidException(ErrorCode.UNSUPPORTED_RAID_DIFFICULTY);
            };
            case KAZEROTH_STAGE1_EGIR -> switch (difficulty){
                case NORMAL -> 1660;
                case HARD -> 1680;
                default -> throw new RaidException(ErrorCode.UNSUPPORTED_RAID_DIFFICULTY);
            };
            case KAZEROTH_STAGE2_ABRELSHUD -> switch (difficulty){
                case NORMAL -> 1670;
                case HARD -> 1690;
                default -> throw new RaidException(ErrorCode.UNSUPPORTED_RAID_DIFFICULTY);
            };
        };
    }

    public int getRequiredItemLevelForStage(Difficulty difficulty, int stage){
        if (!isValidStage(stage)) {
            throw new RaidException(ErrorCode.INVALID_RAID_STAGE);
        }

        if (this == ABRELSHUD && difficulty == Difficulty.NORMAL) {
            return switch (stage) {
                case 1 -> 1490;
                case 2 -> 1500;
                case 3 -> 1520;
                case 4 -> 1540;
                default -> throw new RaidException(ErrorCode.INVALID_RAID_STAGE);
            };
        }

        if (this == ABRELSHUD && difficulty == Difficulty.HARD) {
            return switch (stage) {
                case 1, 2 -> 1540;
                case 3 -> 1550;
                case 4 -> 1560;
                default -> throw new RaidException(ErrorCode.INVALID_RAID_STAGE);
            };
        }

        return getRequiredItemLevel(difficulty);
    }

    public boolean isValidStage(int stage){
        return stage > 0 && stage <= this.stageCount;
    }

    public boolean canEnterWithDifficulty(Difficulty difficulty) {
        return switch (this) {
            case VALTAN, VYKAS, ABRELSHUD -> difficulty == Difficulty.NORMAL ||
                    difficulty == Difficulty.HARD ||
                    difficulty == Difficulty.HELL;

            case KOUKUSATON -> difficulty == Difficulty.NORMAL ||
                    difficulty == Difficulty.HELL;

            case ILLIAKKAN, KAMEN,
                    KAYANGEL, IVORYTOWER,
                    ECHIDNA, KAZEROTH_STAGE1_EGIR, KAZEROTH_STAGE2_ABRELSHUD -> difficulty == Difficulty.NORMAL ||
                    difficulty == Difficulty.HARD;

            case BEHEMOTH -> difficulty == Difficulty.NORMAL;
        };
    }
}
