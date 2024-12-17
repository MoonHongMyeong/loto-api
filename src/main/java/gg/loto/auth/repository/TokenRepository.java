package gg.loto.auth.repository;

import gg.loto.auth.domain.Token;
import gg.loto.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUser(User user);
    Optional<Token> findByAccessToken(String accessToken);

    @Query("SELECT t FROM Token t WHERE t.user.id = :userId")
    Optional<Token> findByUserId(@Param("userId") Long userId);

    void deleteByUser(User user);
}