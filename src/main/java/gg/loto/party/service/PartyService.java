package gg.loto.party.service;

import gg.loto.character.domain.Characters;
import gg.loto.character.service.CharactersService;
import gg.loto.global.exception.ErrorCode;
import gg.loto.party.domain.Party;
import gg.loto.party.exception.PartyException;
import gg.loto.party.mapper.PartyMapper;
import gg.loto.party.repository.PartyMemberRepository;
import gg.loto.party.repository.PartyRepository;
import gg.loto.party.web.dto.*;
import gg.loto.user.domain.User;
import gg.loto.user.service.UserFindDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartyService {
    private final PartyRepository partyRepository;
    private final PartyMemberRepository partyMemberRepository;
    private final PartyMapper partyMapper;
    private final PartyFindDao partyFindDao;
    private final UserFindDao userFindDao;
    private final CharactersService characterService;

    @Transactional
    public PartyResponse createParty(User user, PartySaveRequest dto) {
        validateDuplicationPartyNameAndUserId(dto.getName(), user.getId());

        Party savedParty = partyRepository.save(dto.toEntity(user));

        return PartyResponse.of(savedParty);
    }

    private void validateDuplicationPartyNameAndUserId(String name, Long userId) {
        partyRepository.findByNameAndUserId(name, userId)
                .ifPresent(character -> {
                    throw new PartyException(ErrorCode.EXISTS_PARTY);
                });
    }

    @Transactional
    public PartyResponse updateParty(User user, Long partyId, PartyUpdateRequest dto) {
        Party party = partyFindDao.findPartyById(partyId);
        if (!party.isPartyLeader(user)) throw new PartyException(ErrorCode.NOT_LEADER);

        party.update(dto);

        return PartyResponse.of(party);
    }

    @Transactional
    public PartyResponse transferLeadership(User user, Long partyId, Long userId) {
        Party party = partyFindDao.findPartyById(partyId);
        if (!party.isPartyLeader(user)) throw new PartyException(ErrorCode.NOT_LEADER);

        User newLeader = userFindDao.findById(userId);
        party.transferLeadership(newLeader);

        return PartyResponse.of(party);
    }

    @Transactional
    public PartyResponse joinParty(User user, Long partyId, PartyMemberRequest dto) {
        Party party = partyFindDao.findPartyById(partyId);
        
        if (!party.isPartyMember(user)) {
            validatePartyCapacity(party);
        }
    
        List<Characters> characters = characterService.findAllById(dto.getCharacters());
        
        if (dto.getCharacters().size() != characters.size()){
            throw new PartyException(ErrorCode.NOT_EXISTS_CHARACTER);
        }

        characterService.validateCharacterOwnership(characters, user);

        if (isAlreadyJoinedCharacter(party, characters)){
            throw new PartyException(ErrorCode.DUPLICATE_CHARACTER_JOIN);
        }

        characters.forEach(party::addMember);

        return PartyResponse.of(party);
    }

    @Transactional(readOnly = true)
        private boolean isAlreadyJoinedCharacter(Party party, List<Characters> characters) {
        Set<Long> characterIds = characters.stream()
                .map(Characters::getId)
                .collect(Collectors.toSet());

        return partyMapper.isAlreadyJoinedCharacter(party.getId(), characterIds);
    }

    @Transactional(readOnly = true)
    private void validatePartyCapacity(Party party) {
        int currentJoinMemberSize = partyMapper.getJoinedMemberSize(party.getId());

        if(party.getCapacity() < currentJoinMemberSize + 1){
            throw new PartyException(ErrorCode.PARTY_CAPACITY_EXCEEDED);
        }
    }

    @Transactional
    public void leaveParty(User user, Long partyId, PartyMemberRequest dto) {
        Party party = partyFindDao.findPartyById(partyId);
        if (!party.isPartyMember(user)) {
            throw new PartyException(ErrorCode.NOT_PARTY_MEMBER);
        }

        List<Characters> characters = characterService.findAllById(dto.getCharacters());
        if (dto.getCharacters().size() != characters.size()) {
            throw new PartyException(ErrorCode.NOT_EXISTS_CHARACTER);
        }

        validatePartyMemberLeave(party, user, characters);

        party.removeMembers(characters);
    }

    private void validatePartyMemberLeave(Party party, User user, List<Characters> characters) {
        characterService.validateCharacterOwnership(characters, user);
        
        if (!isAlreadyJoinedCharacter(party, characters)) {
            throw new PartyException(ErrorCode.NOT_EXISTS_CHARACTER);
        }
    
        if (party.isPartyLeader(user)) {
            validatePartyLeaderLeave(party, characters);
        }
    }

    @Transactional(readOnly = true)
    private void validatePartyLeaderLeave(Party party, List<Characters> characters) {
        int partyLeaderCharactersSize = partyMapper.getPartyLeaderCharactersSize(party.getId());
        if (partyLeaderCharactersSize <= characters.size()) {
            throw new PartyException(ErrorCode.PARTY_LEADER_MINIMUM_CHARACTER_REQUIRED);
        }
    }

    @Transactional
    public void kickMember(User user, Long partyId, Long userId) {
        Party party = partyFindDao.findPartyById(partyId);
        if (!party.isPartyLeader(user)) throw new PartyException(ErrorCode.NOT_LEADER);
        if (party.getUser().getId().equals(userId)) throw new PartyException(ErrorCode.CANNOT_KICK_PARTY_LEADER);

        User targetUser = userFindDao.findById(userId);

        if (!party.isPartyMember(targetUser)) throw new PartyException(ErrorCode.TARGET_NOT_PARTY_MEMBER);

        partyMemberRepository.deleteByPartyIdAndUserId(party.getId(), userId);
    }

    @Transactional
    public void removeParty(User user, Long partyId) {
        Party party = partyFindDao.findPartyById(partyId);
        if (!party.isPartyLeader(user)) throw new PartyException(ErrorCode.NOT_LEADER);

        int joinedMemberSize = partyMapper.getJoinedMemberSize(partyId);
        if (joinedMemberSize > 1) {
            throw new PartyException(ErrorCode.CANNOT_DELETE_ACTIVE_PARTY);
        }

        partyRepository.delete(party);
    }

    @Transactional(readOnly = true)
    public List<PartyListResponse> getMyParties(User user) {
        return partyMapper.findMyParties(user.getId());
    }

    @Transactional(readOnly = true)
    public PartyResponse getParty(User user, Long partyId) {
        Party party = partyFindDao.findPartyById(partyId);

        if (!party.isPartyMember(user)) {
            throw new PartyException(ErrorCode.NOT_PARTY_MEMBER);
        }

        return PartyResponse.of(party);
    }

    @Transactional(readOnly = true)
    public PartyMemberCharactersResponse getPartyMemberCharacters(User user, Long partyId, Long lastCharacterId, boolean isMobile) {
        Party party = partyFindDao.findPartyById(partyId);

        if (!party.isPartyMember(user)) {
            throw new PartyException(ErrorCode.NOT_PARTY_MEMBER);
        }

        int pageContentSize = isMobile ? 10: 30;
        int fetchSize = pageContentSize + 1; // 다음 페이지 확인을 위해 실제로 조회할 데이터 수

        List<MemberCharacters> fetchedCharacters = partyMapper.findMemberCharactersPaging(
                partyId,
                lastCharacterId,
                fetchSize
        );

        boolean hasNextPage = fetchedCharacters.size() > pageContentSize;
        List<MemberCharacters> pageCharacters = hasNextPage ?
                fetchedCharacters.subList(0, pageContentSize) : fetchedCharacters;

        return PartyMemberCharactersResponse.of(party, pageCharacters, hasNextPage);
    }
}
