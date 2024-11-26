package gg.loto.party.service;

import gg.loto.character.domain.Characters;
import gg.loto.character.service.CharactersService;
import gg.loto.global.auth.dto.SessionUser;
import gg.loto.party.domain.Party;
import gg.loto.party.mapper.PartyMapper;
import gg.loto.party.repository.PartyInviteCodesRepository;
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
    private final PartyInviteCodesRepository partyInviteCodesRepository;
    private final PartyMapper partyMapper;
    private final UserFindDao userFindDao;
    private final PartyFindDao partyFindDao;
    private final CharactersService characterService;

    @Transactional
    public PartyResponse createParty(SessionUser sessionUser, PartySaveRequest dto) {
        User user = userFindDao.getCurrentUser(sessionUser);
        validateDuplicationPartyNameAndUserId(dto.getName(), user.getId());

        Party savedParty = partyRepository.save(dto.toEntity(user));

        return PartyResponse.of(savedParty);
    }

    private void validateDuplicationPartyNameAndUserId(String name, Long userId) {
        partyRepository.findByNameAndUserId(name, userId)
                .ifPresent(character -> {
                    throw new IllegalArgumentException("이미 존재하는 공유방입니다.");
                });
    }

    @Transactional
    public PartyResponse updateParty(SessionUser sessionUser, Long partyId, PartyUpdateRequest dto) {
        User user = userFindDao.getCurrentUser(sessionUser);
        Party party = partyFindDao.findPartyById(partyId);
        if (!party.isPartyLeader(user)) throw new RuntimeException("권한이 없는 요청입니다.");

        party.update(dto);

        return PartyResponse.of(party);
    }

    @Transactional
    public PartyResponse transferLeadership(SessionUser sessionUser, Long partyId, Long userId) {
        User user = userFindDao.getCurrentUser(sessionUser);
        Party party = partyFindDao.findPartyById(partyId);
        if (!party.isPartyLeader(user)) throw new RuntimeException("권한이 없는 요청입니다.");

        User newLeader = userFindDao.findById(userId);
        party.transferLeadership(newLeader);

        return PartyResponse.of(party);
    }

    @Transactional
    public PartyResponse joinParty(SessionUser sessionUser, Long partyId, PartyMemberRequest dto) {
        User user = userFindDao.getCurrentUser(sessionUser);
        Party party = partyFindDao.findPartyById(partyId);
        
        if (!party.isPartyMember(user)) {
            validatePartyCapacity(party);
        }
    
        List<Characters> characters = characterService.findAllById(dto.getCharacters());
        
        if (dto.getCharacters().size() != characters.size()){
            throw new RuntimeException("존재하지 않는 캐릭터가 포함되어 있습니다.");
        }

        characterService.validateCharacterOwnership(characters, user);

        if (isAlreadyJoinedCharacter(party, characters)){
            throw new RuntimeException("중복된 캐릭터 참여입니다.");
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
            throw new RuntimeException("공유방 인원 제한이 모두 차 입장할 수 없습니다.");
        }
    }

    @Transactional
    public void leaveParty(SessionUser sessionUser, Long partyId, PartyMemberRequest dto) {
        User user = userFindDao.getCurrentUser(sessionUser);
        Party party = partyFindDao.findPartyById(partyId);
        if (!party.isPartyMember(user)) {
            throw new IllegalArgumentException("참여한 공유방이 아닙니다.");
        }

        List<Characters> characters = characterService.findAllById(dto.getCharacters());
        if (dto.getCharacters().size() != characters.size()) {
            throw new IllegalArgumentException("존재하지 않는 캐릭터가 포함되어 있습니다.");
        }

        validatePartyMemberLeave(party, user, characters);

        party.removeMembers(characters);
    }

    private void validatePartyMemberLeave(Party party, User user, List<Characters> characters) {
        characterService.validateCharacterOwnership(characters, user);
        
        if (!isAlreadyJoinedCharacter(party, characters)) {
            throw new IllegalArgumentException("공유방에 참여하지 않은 캐릭터가 존재합니다.");
        }
    
        if (party.isPartyLeader(user)) {
            validatePartyLeaderLeave(party, characters);
        }
    }

    @Transactional(readOnly = true)
    private void validatePartyLeaderLeave(Party party, List<Characters> characters) {
        int partyLeaderCharactersSize = partyMapper.getPartyLeaderCharactersSize(party.getId());
        if (partyLeaderCharactersSize <= characters.size()) {
            throw new IllegalArgumentException("방장은 최소 한 캐릭터는 소유해야 합니다.\n공유방을 떠나려면 다른 사용자에게 방장을 위임해주세요.");
        }
    }

    @Transactional
    public void kickMember(SessionUser sessionUser, Long partyId, Long userId) {
        User user = userFindDao.getCurrentUser(sessionUser);
        Party party = partyFindDao.findPartyById(partyId);
        if (!party.isPartyLeader(user)) throw new RuntimeException("권한이 없는 요청입니다.");
        if (party.isPartyLeader(user)) throw new IllegalArgumentException("방장을 강제 퇴장시킬 수 없습니다.");
    
        User targetUser = userFindDao.findById(userId);
        if (!party.isPartyMember(targetUser)) throw new IllegalArgumentException("해당 유저는 공유방에 속해있지 않습니다.");

        partyMemberRepository.deleteByPartyIdAndUserId(party.getId(), userId);
    }

    @Transactional
    public void removeParty(SessionUser sessionUser, Long partyId) {
        User user = userFindDao.getCurrentUser(sessionUser);
        Party party = partyFindDao.findPartyById(partyId);
        if (!party.isPartyLeader(user)) throw new RuntimeException("권한이 없는 요청입니다.");

        int joinedMemberSize = partyMapper.getJoinedMemberSize(partyId);
        if (joinedMemberSize > 1) {
            throw new RuntimeException("공유방에 다른 사용자가 있으면 삭제가 불가능합니다.");
        }

        partyRepository.delete(party);
    }

    @Transactional(readOnly = true)
    public List<PartyListResponse> getMyParties(SessionUser user) {
        return partyMapper.findMyParties(user.getId());
    }

    @Transactional(readOnly = true)
    public PartyResponse getParty(SessionUser sessionUser, Long partyId) {
        User user = userFindDao.getCurrentUser(sessionUser);
        Party party = partyFindDao.findPartyById(partyId);

        if (!party.isPartyMember(user)) {
            throw new IllegalArgumentException("참여한 공유방이 아닙니다.");
        }

        return PartyResponse.of(party);
    }

    @Transactional(readOnly = true)
    public PartyMemberCharactersResponse getPartyMemberCharacters(SessionUser sessionUser, Long partyId, Long lastCharacterId, boolean isMobile) {
        User user = userFindDao.getCurrentUser(sessionUser);
        Party party = partyFindDao.findPartyById(partyId);

        if (!party.isPartyMember(user)) {
            throw new IllegalArgumentException("참여한 공유방이 아닙니다.");
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
