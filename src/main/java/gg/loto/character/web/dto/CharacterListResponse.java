package gg.loto.character.web.dto;

import gg.loto.character.domain.Characters;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CharacterListResponse {
    private String serverName;
    private String characterName;
    private String characterClassName;
    private String itemMaxLevel;
    private String itemAvgLevel;

    @Builder
    public CharacterListResponse(String serverName, String characterName, String characterClassName, String itemMaxLevel, String itemAvgLevel){
        this.serverName = serverName;
        this.characterName = characterName;
        this.characterClassName = characterClassName;
        this.itemMaxLevel = itemMaxLevel;
        this.itemAvgLevel = itemAvgLevel;
    }

    public static CharacterListResponse of(Characters character){
        return CharacterListResponse.builder()
                .serverName(character.getServerName())
                .characterName(character.getCharacterName())
                .characterClassName(character.getCharacterClassName())
                .itemMaxLevel(character.getItemMaxLevel())
                .itemAvgLevel(character.getItemAvgLevel())
                .build();
    }
}
