package gg.loto.party.vote.service;

import gg.loto.character.domain.Characters;
import gg.loto.character.service.CharacterFindDao;
import gg.loto.global.auth.dto.SessionUser;
import gg.loto.party.domain.Party;
import gg.loto.party.service.PartyFindDao;
import gg.loto.party.vote.domain.PartyRaidVote;
import gg.loto.party.vote.repository.PartyRaidVoteRepository;
import gg.loto.party.vote.web.dto.VoteResponse;
import gg.loto.party.vote.web.dto.VoteSaveRequest;
import gg.loto.raid.entity.Difficulty;
import gg.loto.raid.entity.RaidType;
import gg.loto.user.domain.User;
import gg.loto.user.service.UserFindDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyRaidVoteService {
    private final UserFindDao userFindDao;
    private final PartyFindDao partyFindDao;
    private final CharacterFindDao characterFindDao;
    private final PartyRaidVoteRepository voteRepository;

    @Transactional
    public VoteResponse createVote(SessionUser sessionUser, Long partyId, VoteSaveRequest dto) {
        User user = userFindDao.getCurrentUser(sessionUser);
        Party party = partyFindDao.findPartyById(partyId);
         
        if ( party.isPartyMember(user) ) {
            throw new IllegalArgumentException("참여한 공유방만 투표생성이 가능합니다.");
        }

        if (dto.getVoteExpiresAt().isBefore(dto.getRaidDatetime())) {
            throw new IllegalArgumentException("투표 마감 시간은 레이드 시작 시간 이전이어야 합니다.");
        }

        RaidType raidType = RaidType.valueOf(dto.getRaidType());
        Difficulty difficulty = Difficulty.valueOf(dto.getDifficulty());
        if (!raidType.isValidStage(dto.getTargetGateNumber())) {
            throw new IllegalArgumentException("유효하지 않은 관문 번호입니다.");
        }
        if (!raidType.canEnterWithDifficulty(difficulty)) {
            throw new IllegalArgumentException("유효하지 않은 난이도입니다.");
        }

        Characters character = characterFindDao.findById(dto.getCharacterId());
        if (!character.isOwnership(user)) {
            throw new IllegalArgumentException("참여할 캐릭터는 본인의 캐릭터이어야만 합니다.");
        }

        PartyRaidVote savedVote = voteRepository.save(dto.toEntity(party, user));
        savedVote.addParticipant(character);

        return VoteResponse.of(savedVote);
    }
}
