package gg.loto.party.vote.service;

import gg.loto.character.domain.Characters;
import gg.loto.character.service.CharacterFindDao;
import gg.loto.global.exception.EntityNotFoundException;
import gg.loto.global.exception.ErrorCode;
import gg.loto.party.domain.Party;
import gg.loto.party.exception.VoteException;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyRaidVoteService {
    private final PartyFindDao partyFindDao;
    private final CharacterFindDao characterFindDao;
    private final PartyRaidVoteRepository voteRepository;

    @Transactional
    public VoteResponse createVote(User user, Long partyId, VoteSaveRequest dto) {
        dto.validate();

        Party party = partyFindDao.findPartyById(partyId);
        if (!party.isPartyMember(user)) {
            throw new VoteException(ErrorCode.NOT_PARTY_MEMBER_FOR_VOTE);
        }

        Characters character = characterFindDao.findById(dto.getCharacterId());
        if (!character.isOwnership(user)) {
            throw new VoteException(ErrorCode.NOT_CHARACTER_OWNER);
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

        PartyRaidVote vote = voteRepository.findById(voteId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.VOTE_NOT_FOUND));
        if ( !vote.isCreator(user) ) {
            throw new VoteException(ErrorCode.NOT_VOTE_CREATOR);
        }
        if (!vote.getVoteStatus().equals(VoteStatus.IN_PROGRESS)) {
            throw new VoteException(ErrorCode.VOTE_NOT_IN_PROGRESS);
        }

        vote.update(dto);

        return VoteResponse.of(vote);
    }

    @Transactional
    public VoteResponse cancelVote(User user, Long voteId) {
        PartyRaidVote vote = voteRepository.findById(voteId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.VOTE_NOT_FOUND));

        if ( !vote.isCreator(user) ) {
            throw new VoteException(ErrorCode.NOT_VOTE_CREATOR);
        }
        if (!vote.getVoteStatus().equals(VoteStatus.IN_PROGRESS)) {
            throw new VoteException(ErrorCode.VOTE_NOT_IN_PROGRESS);
        }

        vote.cancel();

        return VoteResponse.of(vote);
    }

    @Transactional
    public VoteResponse joinVote(User user, Long voteId, VoteParticipantSaveRequest dto) {
        PartyRaidVote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.VOTE_NOT_FOUND));

        if (!vote.getVoteStatus().equals(VoteStatus.IN_PROGRESS)) {
            throw new VoteException(ErrorCode.VOTE_NOT_IN_PROGRESS);
        }

        Characters character = characterFindDao.findById(dto.getCharacterId());
        if (!character.isOwnership(user)) {
            throw new VoteException(ErrorCode.NOT_CHARACTER_OWNER);
        }

        PartyRaidVoteParticipant participant = PartyRaidVoteParticipant.builder()
                .vote(vote)
                .character(character)
                .build();

        if (vote.hasParticipant(participant)) {
            throw new VoteException(ErrorCode.VOTE_ALREADY_PARTICIPATED);
        }

        if (vote.isFullParty()) {
            throw new VoteException(ErrorCode.VOTE_PARTY_FULL);
        }

        vote.join(participant);
        return VoteResponse.of(vote);
    }

    @Transactional
    public VoteResponse leaveVote(User user, Long voteId, Long characterId) {
        PartyRaidVote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.VOTE_NOT_FOUND));

        if (!vote.getVoteStatus().equals(VoteStatus.IN_PROGRESS)) {
            throw new VoteException("진행 중인 투표만 취소할 수 있습니다.", ErrorCode.VOTE_NOT_IN_PROGRESS);
        }

        Characters character = characterFindDao.findById(characterId);
        if (!character.isOwnership(user)) {
            throw new VoteException("본인의 캐릭터만 참여 취소할 수 있습니다.", ErrorCode.NOT_CHARACTER_OWNER);
        }

        PartyRaidVoteParticipant participant = PartyRaidVoteParticipant.builder()
                .vote(vote)
                .character(character)
                .build();

        if (!vote.hasParticipant(participant)) {
            throw new VoteException(ErrorCode.CHARACTER_NOT_PARTICIPATED);
        }

        vote.leave(participant);

        return VoteResponse.of(vote);
    }
}
