package gg.loto.party.mapper;

import gg.loto.party.domain.Party;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PartyMapper {
    
    int getJoinedMemberSize(Long partyId);

    boolean isAlreadyJoinedUser(Long partyId, Long userId);

    boolean isAlreadyJoinedCharacter(Long partyId, List<Long> characterIds);
}
