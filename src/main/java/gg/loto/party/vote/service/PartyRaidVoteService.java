package gg.loto.party.vote.service;

import gg.loto.character.domain.Characters;
import gg.loto.character.service.CharacterFindDao;
import gg.loto.party.domain.Party;
import gg.loto.party.service.PartyFindDao;
import gg.loto.party.vote.domain.PartyRaidVote;
import gg.loto.party.vote.domain.PartyRaidVoteParticipant;
import gg.loto.party.vote.domain.VoteStatus;
import gg.loto.party.vote.repository.PartyRaidVoteRepository;
import gg.loto.party.vote.web.dto.VoteParticipantSaveRequest;
import gg.loto.party.vote.web.dto.VoteResponse;
import gg.loto.party.vote.web.dto.VoteSaveRequest;
import gg.loto.party.vote.web.dto.VoteUpdateRequest;
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
    public VoteResponse createVote(User user, Long partyId, VoteSaveRequest dto) {
        dto.validate();

        Party party = partyFindDao.findPartyById(partyId);
        if (!party.isPartyMember(user)) {
            throw new IllegalArgumentException("참여한 공유방만 투표생성이 가능합니다.");
        }

        Characters character = characterFindDao.findById(dto.getCharacterId());
        if (!character.isOwnership(user)) {
            throw new IllegalArgumentException("참여할 캐릭터는 본인의 캐릭터이어야만 합니다.");
        }

        PartyRaidVote savedVote = voteRepository.save(dto.toEntity(party, user));
        savedVote.join(PartyRaidVoteParticipant.builder()
                            .vote(savedVote)
                            .character(character)
                            .build());

        return VoteResponse.of(savedVote);
    }

    @Transactional
    public VoteResponse updateVote(User user, Long voteId, VoteUpdateRequest dto) {
        dto.validate();

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

    @Transactional
    public VoteResponse cancelVote(User user, Long voteId) {
        PartyRaidVote vote = voteRepository.findById(voteId).orElseThrow(() -> new IllegalArgumentException("잘못된 투표 번호입니다."));

        if ( !vote.isCreator(user) ) {
            throw new IllegalArgumentException("투표 취소는 투표 생성자만 가능합니다.");
        }
        if (!vote.getVoteStatus().equals(VoteStatus.IN_PROGRESS)) {
            throw new IllegalArgumentException("투표 상태가 진행중이 아닙니다.");
        }

        vote.cancel();

        return VoteResponse.of(vote);
    }

    @Transactional
    public VoteResponse joinVote(User user, Long voteId, VoteParticipantSaveRequest dto) {
        PartyRaidVote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 투표 번호입니다."));

        if (!vote.getVoteStatus().equals(VoteStatus.IN_PROGRESS)) {
            throw new IllegalArgumentException("진행 중인 투표만 참여할 수 있습니다.");
        }

        Characters character = characterFindDao.findById(dto.getCharacterId());
        if (!character.isOwnership(user)) {
            throw new IllegalArgumentException("본인의 캐릭터만 참여할 수 있습니다.");
        }

        PartyRaidVoteParticipant participant = PartyRaidVoteParticipant.builder()
                .vote(vote)
                .character(character)
                .build();

        if (vote.hasParticipant(participant)) {
            throw new IllegalArgumentException("이미 참여한 캐릭터입니다.");
        }

        if (vote.isFullParty()) {
            throw new IllegalArgumentException("제한 인원이 초과되었습니다.");
        }

        vote.join(participant);
        return VoteResponse.of(vote);
    }

    @Transactional
    public VoteResponse leaveVote(User user, Long voteId, Long characterId) {
        PartyRaidVote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 투표 번호입니다."));

        if (!vote.getVoteStatus().equals(VoteStatus.IN_PROGRESS)) {
            throw new IllegalArgumentException("진행 중인 투표만 취소할 수 있습니다.");
        }

        Characters character = characterFindDao.findById(characterId);
        if (!character.isOwnership(user)) {
            throw new IllegalArgumentException("본인의 캐릭터만 참여 취소할 수 있습니다.");
        }

        PartyRaidVoteParticipant participant = PartyRaidVoteParticipant.builder()
                .vote(vote)
                .character(character)
                .build();

        if (!vote.hasParticipant(participant)) {
            throw new IllegalArgumentException("투표에 참여하지 않은 캐릭터입니다.");
        }

        vote.leave(participant);

        return VoteResponse.of(vote);
    }
}
