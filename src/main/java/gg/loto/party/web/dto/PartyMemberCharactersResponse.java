package gg.loto.party.web.dto;

import gg.loto.party.domain.Party;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PartyMemberCharactersResponse {
    private Long id;
    private boolean hasNext;
    private List<MemberCharacters> memberCharacters;

    @Builder    
    public PartyMemberCharactersResponse(Long id, List<MemberCharacters> memberCharacters, boolean hasNext){
        this.id = id;
        this.memberCharacters = memberCharacters;
        this.hasNext = hasNext;
    }

    public static PartyMemberCharactersResponse of(Party party, List<MemberCharacters> memberCharacters, boolean hasNextPage) {
        return PartyMemberCharactersResponse.builder()
                .id(party.getId())
                .memberCharacters(memberCharacters)
                .hasNext(hasNextPage)
                .build();
    }
}