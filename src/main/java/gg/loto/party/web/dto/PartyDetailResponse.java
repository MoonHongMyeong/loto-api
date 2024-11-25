package gg.loto.party.web.dto;

import gg.loto.party.domain.Party;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PartyDetailResponse {
    private Long id;
    private String nickname;
    private String discordUsername;
    private String partyName;
    private int capacity;
    private String partyType;
    private int currentJoinedCharacters;
    private List<MemberCharacters> memberCharacters;

    @Builder    
    public PartyDetailResponse(Long id, String nickname, String discordUsername, String partyName, int capacity, String partyType, int currentJoinedCharacters, List<MemberCharacters> memberCharacters){
        this.id = id;
        this.nickname = nickname;
        this.discordUsername = discordUsername;
        this.partyName = partyName;
        this.capacity = capacity;
        this.partyType = partyType;
        this.currentJoinedCharacters = currentJoinedCharacters;
        this.memberCharacters = memberCharacters;
    }

    public static PartyDetailResponse of(Party party, List<MemberCharacters> memberCharacters) {
        return PartyDetailResponse.builder()
                .id(party.getId())
                .nickname(party.getUser().getNickname())
                .discordUsername(party.getUser().getDiscordUsername())
                .partyName(party.getName())
                .capacity(party.getCapacity())
                .partyType(party.getPartyType().getTypeKor())
                .currentJoinedCharacters(memberCharacters.size())
                .memberCharacters(memberCharacters)
                .build();
    }
}