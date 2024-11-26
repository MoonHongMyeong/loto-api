package gg.loto.party.domain;

import gg.loto.character.domain.Characters;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PartyMemberId {
    private Long partyId;
    private Long characterId;
    public PartyMemberId(Party party, Characters character) {
        this.partyId = party.getId();
        this.characterId = character.getId();
    }
}
