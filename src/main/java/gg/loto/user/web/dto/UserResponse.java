package gg.loto.user.web.dto;

import gg.loto.auth.domain.Token;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String nickname;
    private String apiKey;
    private String discordUsername;
    private String discordAvatar;
    private String accessToken;
    private String refreshToken;
    private LocalDateTime accessTokenExpiresAt;
    

    @Builder
    UserResponse(Long id, String nickname, String apiKey, String discordUsername, String discordAvatar, String accessToken, String refreshToken, LocalDateTime accessTokenExpiresAt) {
        this.id = id;
        this.nickname = nickname;
        this.apiKey = apiKey;
        this.discordUsername = discordUsername;
        this.discordAvatar = discordAvatar;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
    }

    public static UserResponse from(Token token) {
        return UserResponse.builder()
                .id(token.getUser().getId())
                .nickname(token.getUser().getNickname())
                .apiKey(token.getUser().getApiKey())
                .discordUsername(token.getUser().getDiscordUsername())
                .discordAvatar(token.getUser().getDiscordAvatar())
                .accessToken(token.getAccessToken())
                .accessTokenExpiresAt(token.getAccessTokenExpiresAt())
                .refreshToken(token.getRefreshToken())
                .build();
    }
}
