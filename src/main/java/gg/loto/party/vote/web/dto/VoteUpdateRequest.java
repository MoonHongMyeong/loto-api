package gg.loto.party.vote.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class VoteUpdateRequest extends BaseVoteRequest{
    private String name;

    @NotBlank(message = "레이드 종류는 필수입니다")
    @Pattern(regexp = "^(VALTAN|VYKAS|KOUKUSATON|ABRELSHUD|ILLIAKKAN|KAMEN|KAYANGEL|IVORYTOWER|ECHIDNA|BEHEMOTH|KAZEROTH_STAGE1_EGIR|KAZEROTH_STAGE2_ABRELSHUD)$",
            message = "유효하지 않은 레이드 종류입니다")
    private String raidType;

    @NotBlank(message = "난이도는 필수입니다")
    @Pattern(regexp = "^(NORMAL|HARD|HELL)$",
            message = "유효하지 않은 난이도입니다")
    private String difficulty;

    @Min(value = 1, message = "관문 번호는 1 이상이어야 합니다")
    private int targetGateNumber;

    @NotNull(message = "레이드 시간은 필수입니다")
    @Future(message = "레이드 시간은 현재 시간 이후여야 합니다")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime raidDatetime;

    @NotNull(message = "투표 마감 시간은 필수입니다")
    @Future(message = "투표 마감 시간은 현재 시간 이후여야 합니다")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime voteExpiresAt;

    @Builder
    public VoteUpdateRequest(String name, String raidType, String difficulty, int targetGateNumber, LocalDateTime raidDatetime, LocalDateTime voteExpiresAt) {
        this.name = name;
        this.raidType = raidType;
        this.difficulty = difficulty;
        this.targetGateNumber = targetGateNumber;
        this.raidDatetime = raidDatetime;
        this.voteExpiresAt = voteExpiresAt;
    }

    public void validate(){
        validateRaidRequest(raidType, difficulty, targetGateNumber, voteExpiresAt, raidDatetime);
    }
}
