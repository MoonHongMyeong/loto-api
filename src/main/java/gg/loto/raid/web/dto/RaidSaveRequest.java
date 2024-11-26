package gg.loto.raid.web.dto;

import gg.loto.character.domain.Characters;
import gg.loto.raid.entity.CharacterWeeklyRaid;
import gg.loto.raid.entity.Difficulty;
import gg.loto.raid.entity.RaidType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RaidSaveRequest {
    @NotNull(message = "레이드 보스는 필수입니다")
    private String raidType;

    @NotNull(message = "난이도는 필수입니다")
    private String difficulty;

    @Min(value = 1, message = "스테이지는 1 이상이어야 합니다")
    private int stage;

    @Builder
    public RaidSaveRequest(String raidType, String difficulty, int stage){
        this. raidType = raidType;
        this.difficulty = difficulty;
        this.stage = stage;
    }

    public CharacterWeeklyRaid toEntity(Characters character){
        RaidType raidTypeEnum = RaidType.valueOf(raidType.toUpperCase());
        Difficulty difficultyEnum = Difficulty.valueOf(difficulty.toUpperCase());

        if (!raidTypeEnum.canEnterWithDifficulty(difficultyEnum)) {
            throw new IllegalArgumentException(
                    String.format("해당 레이드(%s)는 %s 난이도를 지원하지 않습니다.",
                            raidTypeEnum, difficultyEnum));
        }

        if (!raidTypeEnum.isValidStage(stage)) {
            throw new IllegalArgumentException(
                    String.format("유효하지 않은 관문 번호입니다. 레이드: %s, 관문: %d",
                            raidTypeEnum, stage));
        }

        int requiredLevel = raidTypeEnum.getRequiredItemLevel(difficultyEnum);
        if (Integer.parseInt(character.getItemMaxLevel()) < requiredLevel) {
            throw new IllegalArgumentException(
                    String.format("아이템 레벨이 부족합니다. 필요 레벨: %d", requiredLevel));
        }

        return CharacterWeeklyRaid.builder()
                .character(character)
                .raidType(raidTypeEnum)
                .difficulty(difficultyEnum)
                .stage(stage)
                .build();
    }
}
