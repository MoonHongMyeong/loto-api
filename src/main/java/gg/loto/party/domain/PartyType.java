package gg.loto.party.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum PartyType {
    FRIENDLY("친목");
    private String typeKor;

    PartyType(String typeKor) {
    }
}
