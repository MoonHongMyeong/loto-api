package gg.loto.party.vote.web;

import gg.loto.global.auth.LoginUser;
import gg.loto.party.vote.service.PartyRaidVoteService;
import gg.loto.party.vote.web.dto.VoteParticipantSaveRequest;
import gg.loto.party.vote.web.dto.VoteResponse;
import gg.loto.party.vote.web.dto.VoteSaveRequest;
import gg.loto.party.vote.web.dto.VoteUpdateRequest;
import gg.loto.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/party/{partyId}/vote")
@RequiredArgsConstructor
public class PartyRaidVoteApiController {
    private final PartyRaidVoteService voteService;

    @PostMapping
    public ResponseEntity<VoteResponse> createVote(@LoginUser User user, @PathVariable(name = "partyId") Long partyId, @Valid @RequestBody VoteSaveRequest dto){
        return ResponseEntity.ok(voteService.createVote(user, partyId, dto));
    }

    @PutMapping("/{voteId}")
    public ResponseEntity<VoteResponse> updateVote(@LoginUser User user, @PathVariable(name = "voteId") Long voteId, @Valid @RequestBody VoteUpdateRequest dto){
        return ResponseEntity.ok(voteService.updateVote(user, voteId, dto));
    }

    @PatchMapping("/{voteId}/cancel")
    public ResponseEntity<VoteResponse> cancelVote(@LoginUser User user, @PathVariable(name = "voteId") Long voteId){
        return ResponseEntity.ok(voteService.cancelVote(user, voteId));
    }

    @PostMapping("/{voteId}/participants")
    public ResponseEntity<VoteResponse> joinVote(@LoginUser User user, @PathVariable(name = "voteId") Long voteId, @Valid @RequestBody VoteParticipantSaveRequest dto){
        return ResponseEntity.ok(voteService.joinVote(user, voteId, dto));
    }

    @DeleteMapping("/{voteId}/participants/{characterId}")
    public ResponseEntity<VoteResponse> leaveVote(@LoginUser User user, @PathVariable(name = "voteId") Long voteId, @PathVariable(name = "characterId") Long characterId){
        return ResponseEntity.ok(voteService.leaveVote(user, voteId, characterId));
    }
}
