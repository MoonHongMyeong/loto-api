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

    @Builder
    public UserResponse(Long id, String nickname, String apiKey, String discordUsername) {
        this.id = id;
        this.nickname = nickname;
        this.apiKey = apiKey;
        this.discordUsername = discordUsername;
    }

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .apiKey(user.getApiKey())
                .discordUsername(user.getDiscordUsername())
                .build();
    }
}
