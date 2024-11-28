package gg.loto.auth.repository;

import gg.loto.auth.domain.Token;
import gg.loto.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUser(User user);
}