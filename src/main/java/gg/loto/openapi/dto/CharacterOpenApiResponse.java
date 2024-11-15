package gg.loto.openapi.dto;

import lombok.Getter;

@Getter
public class CharacterOpenApiResponse {
    private String ServerName;
    private String CharacterName;
    private int CharacterLevel;
    private String CharacterClassName;
    private String ItemAvgLevel;
    private String ItemMaxLevel;
    private String CharacterImage;
}
