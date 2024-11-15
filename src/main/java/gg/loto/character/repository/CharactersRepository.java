package gg.loto.character.repository;

import gg.loto.character.domain.Characters;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CharactersRepository extends JpaRepository<Characters, Long> {
    List<Characters> findAllByUserIdOrderByItemMaxLevelDesc(Long id);

    Optional<Characters> findByCharacterNameAndUserId(String characterName, Long userId);

    Optional<Characters> findByIdAndUserId(Long characterId, Long id);
}
