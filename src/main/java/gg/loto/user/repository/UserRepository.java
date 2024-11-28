package gg.loto.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.loto.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByDiscordId(String discordId);
}
