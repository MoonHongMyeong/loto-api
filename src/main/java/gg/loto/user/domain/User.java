package gg.loto.user.domain;

import gg.loto.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "api_key", length = 500)
    private String apiKey;

    @Column(name = "discord_id")
    private String discordId;

    @Column(name = "discord_username")
    private String discordUsername;

    @Column(name = "discord_avatar")
    private String discordAvatar;

    @Builder
    public User(String email, String password, String nickname, String apiKey, String discordId, String discordUsername, String discordAvatar) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.apiKey = apiKey;
        this.discordId = discordId;
        this.discordUsername = discordUsername;
        this.discordAvatar = discordAvatar;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void changeNickname(String newNickname) {
        this.nickname = newNickname;
    }

}