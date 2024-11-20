package gg.loto.party.service;

import gg.loto.global.auth.dto.SessionUser;
import gg.loto.party.domain.Party;
import gg.loto.party.repository.PartyRepository;
import gg.loto.party.web.dto.PartyResponse;
import gg.loto.party.web.dto.PartySaveRequest;
import gg.loto.party.web.dto.PartyUpdateRequest;
import gg.loto.user.domain.User;
import gg.loto.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        Party party = partyRepository.findById(partyId).orElseThrow(() -> {
            throw new IllegalArgumentException("잘못된 공유방 번호 입니다.");
        });

        if (party.getUser().getId() != user.getId()){
            throw new RuntimeException("권한이 없는 요청입니다.");
        }

        party.update(dto);

        return PartyResponse.of(party);
    }
}
