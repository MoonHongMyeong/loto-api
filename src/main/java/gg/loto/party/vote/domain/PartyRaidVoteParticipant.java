package gg.loto.party.vote.domain;

import gg.loto.character.domain.Characters;
import gg.loto.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Objects;

@Getter
@Entity
@Table(name = "party_raid_vote_participants")
@NoArgsConstructor
public class PartyRaidVoteParticipant extends BaseEntity {
    @EmbeddedId
    private PartyRaidVoteParticipantId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("voteId")
    @JoinColumn(name = "vote_id", nullable = false)
    private PartyRaidVote vote;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("characterId")
    @JoinColumn(name = "character_id", nullable = false)
    private Characters character;

    @Builder
    public PartyRaidVoteParticipant(PartyRaidVote vote, Characters character){
        this.id = new PartyRaidVoteParticipantId(vote.getId(), character.getId());
        this.vote = vote;
        this.character = character;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PartyRaidVoteParticipant)) return false;
        PartyRaidVoteParticipant that = (PartyRaidVoteParticipant) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
