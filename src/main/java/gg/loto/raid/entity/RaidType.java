package gg.loto.raid.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum RaidType {
    VALTAN("발탄", 2),
    VYKAS("비아키스", 2),
    KOUKUSATON("쿠크세이튼", 3),
    ABRELSHUD("아브렐슈드", 4),
    ILLIAKKAN("일리아칸", 3),
    KAMEN("카멘", 4),
    KAYANGEL("카양겔", 3),
    IVORYTOWER("상아탑", 3),
    ECHIDNA("에키드나", 2),
    BEHEMOTH("베히모스", 2),
    KAZEROTH_STAGE1_EGIR("에기르", 2),
    KAZEROTH_STAGE2_ABRELSHUD("아브렐슈드2막", 2);

    private String bossNameKor;
    private int stageCount;

    RaidType(String bossNameKor, int stageCount) {
        this.bossNameKor = bossNameKor;
        this.stageCount = stageCount;
    }

    public int getRequiredItemLevel(Difficulty difficulty){
        return switch (this){
            case VALTAN -> switch(difficulty){
                case NORMAL -> 1415;
                case HARD -> 1445;
                default -> throw new IllegalArgumentException("지원하지 않는 난이도입니다");
            };
            case VYKAS -> switch (difficulty){
                case NORMAL -> 1430;
                case HARD -> 1460;
                default -> throw new IllegalArgumentException("지원하지 않는 난이도입니다");
            };
            case KOUKUSATON -> switch (difficulty){
                case NORMAL -> 1475;
                default -> throw new IllegalArgumentException("지원하지 않는 난이도입니다");
            };
            case ABRELSHUD -> switch (difficulty){
                case NORMAL -> 1490;
                case HARD -> 1540;
                default -> throw new IllegalArgumentException("지원하지 않는 난이도입니다");
            };
            case ILLIAKKAN -> switch (difficulty){
                case NORMAL -> 1580;
                case HARD -> 1600;
                default -> throw new IllegalArgumentException("지원하지 않는 난이도입니다");
            };
            case KAMEN -> switch(difficulty){
                case NORMAL -> 1610;
                case HARD -> 1630;
                default -> throw new IllegalArgumentException("지원하지 않는 난이도입니다");
            };
            case KAYANGEL -> switch (difficulty){
                case NORMAL -> 1540;
                case HARD -> 1580;
                default -> throw new IllegalArgumentException("지원하지 않는 난이도입니다");
            };
            case IVORYTOWER -> switch (difficulty){
                case NORMAL -> 1600;
                case HARD -> 1620;
                default -> throw new IllegalArgumentException("지원하지 않는 난이도입니다");
            };
            case ECHIDNA -> switch (difficulty){
                case NORMAL -> 1620;
                case HARD -> 1640;
                default -> throw new IllegalArgumentException("지원하지 않는 난이도입니다");
            };
            case BEHEMOTH -> switch (difficulty){
                case NORMAL -> 1640;
                default -> throw new IllegalArgumentException("지원하지 않는 난이도입니다");
            };
            case KAZEROTH_STAGE1_EGIR -> switch (difficulty){
                case NORMAL -> 1660;
                case HARD -> 1680;
                default -> throw new IllegalArgumentException("지원하지 않는 난이도입니다");
            };
            case KAZEROTH_STAGE2_ABRELSHUD -> switch (difficulty){
                case NORMAL -> 1670;
                case HARD -> 1690;
                default -> throw new IllegalArgumentException("지원하지 않는 난이도입니다");
            };
        };
    }

    public int getRequiredItemLevelForStage(Difficulty difficulty, int stage){
        if (!isValidStage(stage)) {
            throw new IllegalArgumentException("유효하지 않은 관문입니다.");
        }

        if (this == ABRELSHUD && difficulty == Difficulty.NORMAL) {
            return switch (stage) {
                case 1 -> 1490;
                case 2 -> 1500;
                case 3 -> 1520;
                case 4 -> 1540;
                default -> throw new IllegalArgumentException("유효하지 않은 관문입니다.");
            };
        }

        if (this == ABRELSHUD && difficulty == Difficulty.HARD) {
            return switch (stage) {
                case 1, 2 -> 1540;
                case 3 -> 1550;
                case 4 -> 1560;
                default -> throw new IllegalArgumentException("유효하지 않은 관문입니다.");
            };
        }

        return getRequiredItemLevel(difficulty);
    }

    public boolean isValidStage(int stage){
        return stage > 0 && stage <= this.stageCount;
    }

    public boolean canEnterWithDifficulty(Difficulty difficulty) {
        return switch (this) {
            case VALTAN, VYKAS -> difficulty == Difficulty.NORMAL ||
                    difficulty == Difficulty.HARD ||
                    difficulty == Difficulty.HELL;

            case KOUKUSATON -> difficulty == Difficulty.NORMAL ||
                    difficulty == Difficulty.HELL;

            case ABRELSHUD, ILLIAKKAN, KAMEN,
                    KAYANGEL, IVORYTOWER,
                    ECHIDNA, KAZEROTH_STAGE1_EGIR, KAZEROTH_STAGE2_ABRELSHUD -> difficulty == Difficulty.NORMAL ||
                    difficulty == Difficulty.HARD;

            case BEHEMOTH -> difficulty == Difficulty.NORMAL;
        };
    }
}
