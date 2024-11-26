package gg.loto.party.service;

import gg.loto.global.auth.dto.SessionUser;
import gg.loto.party.domain.Party;
import gg.loto.party.domain.PartyInviteCodes;
import gg.loto.party.repository.PartyInviteCodesRepository;
import gg.loto.party.web.dto.InviteCodeResponse;
import gg.loto.party.web.dto.PartyInviteCodeCreateRequest;
import gg.loto.party.web.dto.PartyResponse;
import gg.loto.user.domain.User;
import gg.loto.user.service.UserFindDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PartyInviteCodeService {

    private final PartyInviteCodesRepository inviteCodesRepository;
    private final PartyFindDao partyFindDao;
    private final UserFindDao userFindDao;

    @Transactional
    public InviteCodeResponse createInviteCode(SessionUser sessionUser, PartyInviteCodeCreateRequest dto) {
        User user = userFindDao.getCurrentUser(sessionUser);
        Party party = partyFindDao.findPartyById(dto.getPartyId());

        if (!party.isPartyLeader(user)){
            throw new RuntimeException("초대 권한이 없습니다.");
        }

        return InviteCodeResponse.of(party.generateInviteCode());
    }

    @Transactional(readOnly = true)
    public PartyResponse getPartyByInviteCode(String code) {
        PartyInviteCodes inviteCode = inviteCodesRepository.findByCode(UUID.fromString(code))
                .orElseThrow(() -> new IllegalArgumentException("잘못된 초대코드 입니다."));

        if (inviteCode.isExpired()) {
            throw new RuntimeException("유효기간이 만료된 코드입니다.");
        }

        Party party = inviteCode.getParty();

        return PartyResponse.of(party);
    }
}
