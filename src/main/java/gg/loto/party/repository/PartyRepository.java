package gg.loto.party.repository;

import gg.loto.party.domain.Party;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartyRepository extends JpaRepository<Party, Long> {
    Optional<Party> findByNameAndUserId(String name, Long userId);
}