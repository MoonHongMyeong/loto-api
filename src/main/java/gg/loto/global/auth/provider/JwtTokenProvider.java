package gg.loto.global.auth.provider;

import gg.loto.global.auth.exception.TokenException;
import gg.loto.global.exception.ErrorCode;
import gg.loto.user.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.access-token-expiration}")
    private long ACCESS_TOKEN_EXPIRATION;

    @Value("${jwt.refresh-token-expiration}")
    private long REFRESH_TOKEN_EXPIRATION;

    private SecretKey key;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getEncoder()
                            .encode(SECRET_KEY.getBytes());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .claims()
                    .add("id", user.getId())
                    .add("discordId", user.getDiscordId())
                    .add("nickname", user.getNickname())
                    .and()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .claims()
                    .add("id", user.getId())
                    .and()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new TokenException(token, ErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new TokenException(token, ErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new TokenException(token, ErrorCode.INVALID_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new TokenException(token, ErrorCode.INVALID_TOKEN);
        }
    }

    public Long getUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("id", Long.class);
    }
}
