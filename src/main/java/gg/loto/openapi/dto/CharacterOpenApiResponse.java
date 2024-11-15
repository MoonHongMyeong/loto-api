package gg.loto.openapi.dto;

import lombok.Getter;
import lombok.Builder;
@Getter
public class CharacterOpenApiResponse {
    private String ServerName;
    private String CharacterName;
    private int CharacterLevel;
    private String CharacterClassName;
    private String ItemAvgLevel;
    private String ItemMaxLevel;
    private String CharacterImage;

    @Builder
    public CharacterOpenApiResponse(String ServerName, String CharacterName, int CharacterLevel, String CharacterClassName, String ItemAvgLevel, String ItemMaxLevel, String CharacterImage) {
        this.ServerName = ServerName;
        this.CharacterName = CharacterName;
        this.CharacterLevel = CharacterLevel;
        this.CharacterClassName = CharacterClassName;
        this.ItemAvgLevel = ItemAvgLevel;
        this.ItemMaxLevel = ItemMaxLevel;
        this.CharacterImage = CharacterImage;
    }
}
