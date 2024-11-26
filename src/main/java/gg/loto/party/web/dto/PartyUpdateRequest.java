package gg.loto.party.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Getter
@NoArgsConstructor
public class PartyUpdateRequest {
    @NotBlank
    private String name;
    @Range(min = 2, max = 100, message = "생성되는 공유방의 인원은 2-100명 이어야합니다.")
    @NotNull
    private int capacity;
    @NotBlank
    private String partyType;

    @Builder
    public PartyUpdateRequest(String name, int capacity, String partyType){
        this.name = name;
        this.capacity = capacity;
        this.partyType = partyType;
    }
}
