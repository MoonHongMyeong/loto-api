package gg.loto.party.vote.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PartyRaidVoteParticipantId {
    @Column(name = "vote_id")
    private Long voteId;

    @Column(name = "character_id")
    private Long characterId;
}
