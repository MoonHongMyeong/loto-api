package gg.loto.party.web.dto;

import gg.loto.party.domain.Party;
import gg.loto.party.domain.PartyType;
import gg.loto.user.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Getter
@NoArgsConstructor
public class PartySaveRequest {
    @NotBlank
    private String name;

    @Range(min = 2, max = 100, message = "생성되는 공유방의 인원은 2-100명 이어야합니다.")
    @NotNull
    private int capacity;

    @NotBlank
    private String partyType;

    @Builder
    public PartySaveRequest(String name, int capacity, String partyType){
        this.name = name;
        this.capacity = capacity;
        this.partyType = partyType;
    }

    public Party toEntity(User user){
        return Party.builder()
                .user(user)
                .name(name)
                .capacity(capacity)
                .partyType(PartyType.valueOf(partyType))
                .build();
    }
}
