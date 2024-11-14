package gg.loto.global.auth.dto;

import gg.loto.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SessionUser {
    private Long id;
    private String nickname;
    private String discordId;
    private String discordUsername;
    private String discordAvatar;

    public SessionUser(User user){
        this.id=user.getId();
        this.nickname=user.getNickname();
        this.discordId=user.getDiscordId();
        this.discordUsername=user.getDiscordUsername();
        this.discordAvatar=user.getDiscordAvatar();
    }
}
