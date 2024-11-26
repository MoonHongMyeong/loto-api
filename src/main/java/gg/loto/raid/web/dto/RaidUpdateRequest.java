package gg.loto.raid.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RaidUpdateRequest {
    @NotNull(message = "난이도는 필수입니다")
    private String difficulty;
    @Min(value = 1, message = "스테이지는 1 이상이어야 합니다")
    private int stage;

    @Builder
    public RaidUpdateRequest(String difficulty, int stage){
        this.difficulty = difficulty;
        this.stage = stage;
    }
}
