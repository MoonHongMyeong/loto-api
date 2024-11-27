package gg.loto.raid.repository;

import gg.loto.raid.entity.CharacterWeeklyRaid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RaidRepository extends JpaRepository<CharacterWeeklyRaid, Long> {
}