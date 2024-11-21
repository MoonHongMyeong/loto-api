package gg.loto.party.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.loto.global.auth.dto.SessionUser;
import gg.loto.party.domain.Party;
import gg.loto.party.repository.PartyRepository;
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
    private final UserService userService;

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

    private Party findPartyById(Long partyId) {
        return partyRepository.findById(partyId).orElseThrow( () -> {
           throw new IllegalArgumentException("잘못된 공유방 번호 입니다.");
        });
    }
}
