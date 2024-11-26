package gg.loto.party.repository;

import gg.loto.party.domain.PartyInviteCodes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PartyInviteCodesRepository extends JpaRepository<PartyInviteCodes, UUID> {
    Optional<PartyInviteCodes> findByCode(UUID uuid);
}