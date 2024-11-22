package gg.loto.party.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.loto.character.domain.Characters;
import gg.loto.character.service.CharactersService;
import gg.loto.global.auth.dto.SessionUser;
import gg.loto.party.domain.Party;
import gg.loto.party.mapper.PartyMapper;
import gg.loto.party.repository.PartyRepository;
import gg.loto.party.web.dto.PartyJoinRequest;
import gg.loto.party.web.dto.PartyResponse;
import gg.loto.party.web.dto.PartySaveRequest;
import gg.loto.party.web.dto.PartyUpdateRequest;
import gg.loto.user.domain.User;
import gg.loto.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PartyService {
    private final PartyRepository partyRepository;
    private final PartyMapper partyMapper;
    private final UserService userService;
    private final CharactersService characterService;

    @Transactional
    public PartyResponse createParty(SessionUser sessionUser, PartySaveRequest dto) {
        User user = userService.getCurrentUser(sessionUser);
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
        User user = userService.getCurrentUser(sessionUser);
        Party party = findPartyById(partyId);
        validatePartyLeader(user, party);

        party.update(dto);

        return PartyResponse.of(party);
    }

    @Transactional
    public PartyResponse transferLeadership(SessionUser sessionUser, Long partyId, Long userId) {
        User user = userService.getCurrentUser(sessionUser);
        Party party = findPartyById(partyId);
        validatePartyLeader(user, party);

        User newLeader = userService.findById(userId);
        party.transferLeadership(newLeader);

        return PartyResponse.of(party);
    }

    private void validatePartyLeader(User user, Party party) {
        if (!Objects.equals(party.getUser().getId(), user.getId())){
            throw new RuntimeException("권한이 없는 요청입니다.");
        }
    }

    @Transactional(readOnly = true)
    private Party findPartyById(Long partyId) {
        return partyRepository.findById(partyId).orElseThrow( () -> {
           throw new IllegalArgumentException("잘못된 공유방입니다.");
        });
    }

    @Transactional
    public PartyResponse joinParty(SessionUser sessionUser, Long partyId, PartyJoinRequest dto) {
        User user = userService.getCurrentUser(sessionUser);
        Party party = findPartyById(partyId);
        
        if (!isAlreadyJoinedUser(party, user)) {
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
        List<Long> characterIds = characters.stream()
                .map(Characters::getId)
                .collect(Collectors.toList());

        return partyMapper.isAlreadyJoinedCharacter(party.getId(), characterIds);
    }

    @Transactional(readOnly = true)
    private boolean isAlreadyJoinedUser(Party party, User user) {
        return partyMapper.isAlreadyJoinedUser(party.getId(), user.getId());
    }

    @Transactional(readOnly = true)
    private void validatePartyCapacity(Party party) {
        int currentJoinMemberSize = partyMapper.getJoinedMemberSize(party.getId());
        if(party.getCapacity() < currentJoinMemberSize + 1){
            throw new RuntimeException("공유방 인원 제한이 모두 차 입장할 수 없습니다.");
        }
    }
}
