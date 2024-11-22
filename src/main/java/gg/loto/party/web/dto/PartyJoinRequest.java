package gg.loto.party.web.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PartyJoinRequest {
    @NotEmpty(message = "공유방에 참여할 캐릭터를 지정해야합니다.")
    private List<Long> characters;

    @Builder
    public PartyJoinRequest(List<Long> characters){
        this.characters = characters;
    }
}
