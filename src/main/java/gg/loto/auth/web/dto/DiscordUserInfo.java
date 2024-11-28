package gg.loto.auth.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DiscordUserInfo {
    private String id;
    private String username;
    private String discriminator;
    private String avatar;
    private String email;
    private boolean verified;

    public String getAvatarUrl() {
        if (avatar != null) {
            return String.format("https://cdn.discordapp.com/avatars/%s/%s.png", id, avatar);
        }
        return null;
    }
}
