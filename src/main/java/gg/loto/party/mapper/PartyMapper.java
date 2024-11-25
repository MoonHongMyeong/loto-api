package gg.loto.party.mapper;

import gg.loto.party.web.dto.PartyListResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Mapper
public interface PartyMapper {
    
    int getJoinedMemberSize(Long partyId);

    boolean isAlreadyJoinedUser(Long partyId, Long userId);

    boolean isAlreadyJoinedCharacter(Long partyId, Set<Long> characterIds);

    int getPartyLeaderCharactersSize(Long partyId);

    List<PartyListResponse> findMyParties(Long userId);
}
