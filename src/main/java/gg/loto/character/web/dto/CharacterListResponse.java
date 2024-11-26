package gg.loto.character.web.dto;

import gg.loto.character.domain.Characters;
import gg.loto.raid.entity.CharacterWeeklyRaid;
import gg.loto.raid.web.dto.WeeklyRaidResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class CharacterListResponse {
    private String serverName;
    private String characterName;
    private String characterClassName;
    private String itemMaxLevel;
    private String itemAvgLevel;
    private List<WeeklyRaidResponse> weeklyRaids = new ArrayList<>();

    @Builder
    public CharacterListResponse(String serverName, String characterName, String characterClassName, String itemMaxLevel, String itemAvgLevel, List<WeeklyRaidResponse> weeklyRaids){
        this.serverName = serverName;
        this.characterName = characterName;
        this.characterClassName = characterClassName;
        this.itemMaxLevel = itemMaxLevel;
        this.itemAvgLevel = itemAvgLevel;
        this.weeklyRaids = weeklyRaids;
    }

    public static CharacterListResponse of(Characters character){
        return CharacterListResponse.builder()
                .serverName(character.getServerName())
                .characterName(character.getCharacterName())
                .characterClassName(character.getCharacterClassName())
                .itemMaxLevel(character.getItemMaxLevel())
                .itemAvgLevel(character.getItemAvgLevel())
                .weeklyRaids(character.getWeeklyRaids().stream()
                        .map(WeeklyRaidResponse::of)
                        .collect(Collectors.toList()))
                .build();
    }

}
