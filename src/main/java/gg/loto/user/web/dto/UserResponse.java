package gg.loto.user.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import gg.loto.user.domain.User;

@Getter
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String nickname;
    private String apiKey;
    private String discordUsername;
    private String discordAvatar;

    @Builder
    public UserResponse(Long id, String nickname, String apiKey, String discordUsername, String discordAvatar) {
        this.id = id;
        this.nickname = nickname;
        this.apiKey = apiKey;
        this.discordUsername = discordUsername;
        this.discordAvatar = discordAvatar;
    }

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .apiKey(user.getApiKey())
                .discordUsername(user.getDiscordUsername())
                .discordAvatar(user.getDiscordAvatar())
                .build();
    }
}
