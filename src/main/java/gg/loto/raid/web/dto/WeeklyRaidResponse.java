package gg.loto.raid.web.dto;

import gg.loto.raid.entity.CharacterWeeklyRaid;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class WeeklyRaidResponse {
    private Long id;
    private String raidType;
    private String difficulty;
    private int stage;
    private LocalDateTime updatedAt;

    @Builder
    public WeeklyRaidResponse(Long id, String raidType, String difficulty, int stage, LocalDateTime updatedAt){
        this.id = id;
        this.raidType = raidType;
        this.difficulty = difficulty;
        this.stage = stage;
        this.updatedAt = updatedAt;
    }

    public static WeeklyRaidResponse of(CharacterWeeklyRaid weeklyRaid){
        return WeeklyRaidResponse.builder()
                .id(weeklyRaid.getId())
                .raidType(weeklyRaid.getRaidType().name())
                .difficulty(weeklyRaid.getDifficulty().name())
                .stage(weeklyRaid.getStage())
                .updatedAt(weeklyRaid.getUpdatedAt())
                .build();
    }

}
