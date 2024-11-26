package gg.loto.party.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PartyListResponse {
    private Long partyId;
    private String partyName;
    private String leaderNickname;
    private int capacity;
    private int currentJoinedMembers;
    private String partyType;
}
