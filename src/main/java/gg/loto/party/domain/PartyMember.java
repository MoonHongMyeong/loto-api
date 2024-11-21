package gg.loto.party.domain;

import gg.loto.character.domain.Characters;
import gg.loto.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "party_member")
@NoArgsConstructor
public class PartyMember extends BaseEntity {
    @EmbeddedId
    private PartyMemberId id;

    @MapsId("partyId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id")
    private Party party;

    @MapsId("characterId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    private Characters character;

    @Builder
    public PartyMember(Party party, Characters character){
        this.id = new PartyMemberId(party, character);
        this.party = party;
        this.character = character;
    }
}
