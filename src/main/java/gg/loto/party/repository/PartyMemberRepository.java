package gg.loto.party.repository;

import gg.loto.party.domain.Party;
import gg.loto.party.domain.PartyMember;
import gg.loto.party.domain.PartyMemberId;
import gg.loto.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface PartyMemberRepository extends JpaRepository<PartyMember, PartyMemberId> {
    void deleteByPartyIdAndCharacterIdIn(Long partyId, Set<Long> characters);

    @Query("DELETE FROM PartyMember pm WHERE pm.party.id = :partyId AND pm.character.user.id = :userId")
    @Modifying
    void deleteByPartyIdAndUserId(Long partyId, Long userId);

    boolean existsByPartyAndCharacter_User(Party party, User user);
}