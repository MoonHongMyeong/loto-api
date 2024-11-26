package gg.loto.party.service;

import gg.loto.party.domain.Party;
import gg.loto.party.repository.PartyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyFindDao {
    private final PartyRepository partyRepository;

    @Transactional(readOnly = true)
    public Party findPartyById(Long partyId) {
        return partyRepository.findById(partyId).orElseThrow( () -> {
            throw new IllegalArgumentException("잘못된 공유방입니다.");
        });
    }
}
