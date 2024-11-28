package gg.loto.auth.domain;

import gg.loto.global.entity.BaseEntity;
import gg.loto.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "tokens")
@NoArgsConstructor
public class Token extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 500)
    private String accessToken;

    @Column(nullable = false, length = 500)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime accessTokenExpiresAt;

    @Column(nullable = false)
    private LocalDateTime refreshTokenExpiresAt;

    @Builder
    public Token(User user, String accessToken, String refreshToken,
                 LocalDateTime accessTokenExpiresAt, LocalDateTime refreshTokenExpiresAt) {
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    }

    public void updateAccessToken(String newAccessToken, LocalDateTime expiresAt) {
        this.accessToken = newAccessToken;
        this.accessTokenExpiresAt = expiresAt;
    }

    public boolean isAccessTokenExpired() {
        return LocalDateTime.now().isAfter(this.accessTokenExpiresAt);
    }

    public boolean isRefreshTokenExpired() {
        return LocalDateTime.now().isAfter(this.refreshTokenExpiresAt);
    }


}
