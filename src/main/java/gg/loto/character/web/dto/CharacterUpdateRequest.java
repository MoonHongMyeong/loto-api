package gg.loto.character.web.dto;

import gg.loto.character.domain.Characters;
import gg.loto.user.domain.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CharacterUpdateRequest {
    @NotBlank
    private String serverName;
    @NotBlank
    private String characterName;
    @NotBlank
    private int characterLevel;
    @NotBlank
    private String characterClassName;
    @NotBlank
    private String itemAvgLevel;
    @NotBlank
    private String itemMaxLevel;
    @NotBlank
    private String characterImage;

    @Builder
    public CharacterUpdateRequest(String serverName, String characterName, int characterLevel, String characterClassName, String itemAvgLevel, String itemMaxLevel, String characterImage){
        this.serverName = serverName;
        this.characterName = characterName;
        this.characterLevel = characterLevel;
        this.characterClassName = characterClassName;
        this.itemAvgLevel = itemAvgLevel;
        this.itemMaxLevel = itemMaxLevel;
        this.characterImage = characterImage;
    }
}
