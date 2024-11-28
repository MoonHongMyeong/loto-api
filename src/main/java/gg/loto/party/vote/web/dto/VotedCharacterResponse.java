package gg.loto.party.vote.web.dto;

import gg.loto.party.vote.domain.PartyRaidVoteParticipant;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VotedCharacterResponse {
    private Long characterId;
    private String serverName;
    private String characterName;
    private String characterClassName;
    private String itemMaxLevel;

    @Builder
    public VotedCharacterResponse(Long characterId, String serverName, String characterName, String characterClassName, String itemMaxLevel) {
        this.characterId = characterId;
        this.serverName = serverName;
        this.characterName = characterName;
        this.characterClassName = characterClassName;
        this.itemMaxLevel = itemMaxLevel;
    }

    public static VotedCharacterResponse of(PartyRaidVoteParticipant participant){
        return VotedCharacterResponse.builder()
                .characterId(participant.getCharacter().getId())
                .serverName(participant.getCharacter().getServerName())
                .characterName(participant.getCharacter().getCharacterName())
                .characterClassName(participant.getCharacter().getCharacterClassName())
                .itemMaxLevel(participant.getCharacter().getItemMaxLevel())
                .build();
    }
}
