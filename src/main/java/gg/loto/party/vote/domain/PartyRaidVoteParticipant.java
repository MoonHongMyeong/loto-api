package gg.loto.party.vote.domain;

import gg.loto.character.domain.Characters;
import gg.loto.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "party_raid_vote_participants")
@NoArgsConstructor
public class PartyRaidVoteParticipant extends BaseEntity {
    @EmbeddedId
    private PartyRaidVoteParticipantId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("vote_id")
    @JoinColumn(name = "vote_id", nullable = false)
    private PartyRaidVote vote;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("character_id")
    @JoinColumn(name = "character_id", nullable = false)
    private Characters character;

    @Builder
    public PartyRaidVoteParticipant(PartyRaidVote vote, Characters character){
        this.id = new PartyRaidVoteParticipantId(vote.getId(), character.getId());
        this.vote = vote;
        this.character = character;
    }
}
