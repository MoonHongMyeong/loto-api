package gg.loto.character.web.dto;

import gg.loto.character.domain.Characters;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CharacterResponse {
    private Long id;
    private String serverName;
    private String characterName;
    private String characterClassName;
    private int characterLevel;
    private String characterImage;
    private String itemMaxLevel;
    private String itemAvgLevel;

    @Builder
    public CharacterResponse(Long id, String serverName, String characterName, String characterClassName, int characterLevel, String characterImage, String itemMaxLevel, String itemAvgLevel){
        this.id = id;
        this.serverName = serverName;
        this.characterName = characterName;
        this.characterClassName = characterClassName;
        this.characterLevel = characterLevel;
        this.characterImage = characterImage;
        this.itemMaxLevel = itemMaxLevel;
        this.itemAvgLevel = itemAvgLevel;
    }

    public static CharacterResponse of(Characters character){
        return CharacterResponse.builder()
                .id(character.getId())
                .serverName(character.getServerName())
                .characterName(character.getCharacterName())
                .characterClassName(character.getCharacterClassName())
                .characterLevel(character.getCharacterLevel())
                .characterImage(character.getCharacterImage())
                .itemMaxLevel(character.getItemMaxLevel())
                .itemAvgLevel(character.getItemAvgLevel())
                .build();
    }
}
