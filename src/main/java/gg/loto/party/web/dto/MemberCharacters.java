package gg.loto.party.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberCharacters {
    private Long id;
    private String serverName;
    private String characterName;
    private String characterClassName;
    private String itemMaxLevel;
    private String nickname;
    private String discordUsername;

    @Builder
    public MemberCharacters(Long id, String serverName, String characterName, String characterClassName, String itemMaxLevel, String nickname, String discordUsername){
        this.id = id;
        this.serverName = serverName;
        this.characterName = characterName;
        this.characterClassName = characterClassName;
        this.itemMaxLevel = itemMaxLevel;
        this.nickname = nickname;
        this.discordUsername = discordUsername;
    }
}
