package gg.loto.raid.web.dto;

import gg.loto.raid.entity.CharacterWeeklyRaid;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class WeeklyRaidResponse {
    private String raidType;
    private String difficulty;
    private int stage;
    private LocalDateTime updatedAt;

    @Builder
    public WeeklyRaidResponse(String raidType, String difficulty, int stage, LocalDateTime updatedAt){
        this.raidType = raidType;
        this.difficulty = difficulty;
        this.stage = stage;
        this.updatedAt = updatedAt;
    }

    public static WeeklyRaidResponse of(CharacterWeeklyRaid weeklyRaid){
        return WeeklyRaidResponse.builder()
                .raidType(weeklyRaid.getRaidType().name())
                .difficulty(weeklyRaid.getDifficulty().name())
                .stage(weeklyRaid.getStage())
                .updatedAt(weeklyRaid.getUpdatedAt())
                .build();
    }

}
