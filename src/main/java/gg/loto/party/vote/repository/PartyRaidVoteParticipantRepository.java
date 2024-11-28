package gg.loto.party.vote.repository;

import gg.loto.party.vote.domain.PartyRaidVoteParticipant;
import gg.loto.party.vote.domain.PartyRaidVoteParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRaidVoteParticipantRepository extends JpaRepository<PartyRaidVoteParticipant, PartyRaidVoteParticipantId> {
}