package gg.loto.party.vote.repository;

import gg.loto.party.vote.domain.PartyRaidVote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRaidVoteRepository extends JpaRepository<PartyRaidVote, Long> {
}