package gg.loto.party.repository;

import gg.loto.party.domain.PartyMember;
import gg.loto.party.domain.PartyMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface PartyMemberRepository extends JpaRepository<PartyMember, PartyMemberId> {
    void deleteByPartyIdAndCharacterIdIn(Long partyId, Set<Long> characters);

    void deleteByPartyIdAndUserId(Long id, Long userId);
}