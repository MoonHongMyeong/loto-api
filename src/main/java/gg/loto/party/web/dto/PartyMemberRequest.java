package gg.loto.party.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
public class PartyMemberRequest {
    @NotEmpty(message = "공유방에 참여할 캐릭터를 지정해야합니다.")
    private Set<Long> characters;

    @Builder
    public PartyMemberRequest(@JsonProperty("characters") List<Long> characters){
        this.characters = new HashSet<>(characters);
    }
}
