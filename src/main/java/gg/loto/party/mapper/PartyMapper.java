package gg.loto.party.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Set;

@Mapper
public interface PartyMapper {
    
    int getJoinedMemberSize(Long partyId);

    boolean isAlreadyJoinedUser(Long partyId, Long userId);

    boolean isAlreadyJoinedCharacter(Long partyId, Set<Long> characterIds);

    int getPartyLeaderCharactersSize(Long partyId);
}
