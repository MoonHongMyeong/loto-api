package gg.loto.party.vote.service;

import gg.loto.character.domain.Characters;
import gg.loto.character.service.CharacterFindDao;
import gg.loto.global.auth.dto.SessionUser;
import gg.loto.party.domain.Party;
import gg.loto.party.service.PartyFindDao;
import gg.loto.party.vote.domain.PartyRaidVote;
import gg.loto.party.vote.domain.VoteStatus;
import gg.loto.party.vote.repository.PartyRaidVoteRepository;
import gg.loto.party.vote.web.dto.VoteResponse;
import gg.loto.party.vote.web.dto.VoteSaveRequest;
import gg.loto.party.vote.web.dto.VoteUpdateRequest;
import gg.loto.raid.entity.Difficulty;
import gg.loto.raid.entity.RaidType;
import gg.loto.user.domain.User;
import gg.loto.user.service.UserFindDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PartyRaidVoteService {
    private final UserFindDao userFindDao;
    private final PartyFindDao partyFindDao;
    private final CharacterFindDao characterFindDao;
    private final PartyRaidVoteRepository voteRepository;

    @Transactional
    public VoteResponse createVote(SessionUser sessionUser, Long partyId, VoteSaveRequest dto) {
        dto.validate();

        User user = userFindDao.getCurrentUser(sessionUser);
        Party party = partyFindDao.findPartyById(partyId);
        if (!party.isPartyMember(user)) {
            throw new IllegalArgumentException("참여한 공유방만 투표생성이 가능합니다.");
        }

        Characters character = characterFindDao.findById(dto.getCharacterId());
        if (!character.isOwnership(user)) {
            throw new IllegalArgumentException("참여할 캐릭터는 본인의 캐릭터이어야만 합니다.");
        }

        PartyRaidVote savedVote = voteRepository.save(dto.toEntity(party, user));
        savedVote.addParticipant(character);

        return VoteResponse.of(savedVote);
    }

    @Transactional
    public VoteResponse updateVote(SessionUser sessionUser, Long partyId, Long voteId, VoteUpdateRequest dto) {
        dto.validate();

        User user = userFindDao.getCurrentUser(sessionUser);
        PartyRaidVote vote = voteRepository.findById(voteId).orElseThrow(() -> new IllegalArgumentException("잘못된 투표 번호입니다."));
        if ( !vote.isCreator(user) ) {
            throw new IllegalArgumentException("투표 수정은 투표 생성자만 가능합니다.");
        }
        if (!vote.getVoteStatus().equals(VoteStatus.IN_PROGRESS)) {
            throw new IllegalArgumentException("투표 상태가 진행중이 아닙니다.");
        }

        vote.update(dto);

        return VoteResponse.of(vote);
    }
}
