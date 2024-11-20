package gg.loto.party.web.dto;

import gg.loto.party.domain.Party;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PartyResponse {
    private Long id;
    private String nickname;
    private String discordUsername;
    private String name;
    private int capacity;
    private String partyType;

    @Builder
    public PartyResponse(Long id, String nickname, String discordUsername, String name, int capacity, String partyType){
        this.id = id;
        this.nickname = nickname;
        this.discordUsername = discordUsername;
        this.name = name;
        this.capacity = capacity;
        this.partyType = partyType;
    }

    public static PartyResponse of(Party party) {
        return PartyResponse.builder()
                .id(party.getId())
                .nickname(party.getUser().getNickname())
                .discordUsername(party.getUser().getDiscordUsername())
                .name(party.getName())
                .capacity(party.getCapacity())
                .partyType(party.getPartyType().getTypeKor())
                .build();
    }
}
