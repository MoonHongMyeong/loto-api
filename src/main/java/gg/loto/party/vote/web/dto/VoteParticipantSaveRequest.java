package gg.loto.party.vote.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VoteParticipantSaveRequest {
    @NotNull(message = "참여할 캐릭터는 필수입니다")
    private Long characterId;

    @Builder
    public VoteParticipantSaveRequest(Long characterId) {
        this.characterId = characterId;
    }
}
