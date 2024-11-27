package gg.loto.party.vote.web.dto;

import gg.loto.party.vote.domain.PartyRaidVote;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class VoteResponse {
    private Long id;
    private String name;
    private String partyName;
    private String creatorName;
    private String raidType;
    private String raidTypeValue;
    private String difficulty;
    private String difficultyValue;
    private int targetGateNumber;
    private LocalDateTime raidDatetime;
    private LocalDateTime voteExpiresAt;
    private String voteStatus;
    private List<VotedCharacterResponse> votedCharacters;

    @Builder
    public VoteResponse(Long id, String name, String partyName, String creatorName, String raidType, String raidTypeValue, String difficulty, String difficultyValue, int targetGateNumber, LocalDateTime raidDatetime, LocalDateTime voteExpiresAt, String voteStatus, List<VotedCharacterResponse> votedCharacters) {
        this.id = id;
        this.name = name;
        this.partyName = partyName;
        this.creatorName = creatorName;
        this.raidType = raidType;
        this.raidTypeValue = raidTypeValue;
        this.difficulty = difficulty;
        this.difficultyValue = difficultyValue;
        this.targetGateNumber = targetGateNumber;
        this.raidDatetime = raidDatetime;
        this.voteExpiresAt = voteExpiresAt;
        this.voteStatus = voteStatus;
        this.votedCharacters = votedCharacters;
    }

    public static VoteResponse of(PartyRaidVote vote) {
        return VoteResponse.builder()
                .id(vote.getId())
                .name(vote.getName())
                .partyName(vote.getParty().getName())
                .creatorName(vote.getCreator().getNickname())
                .raidType(vote.getRaidType().getBossNameKor())
                .raidTypeValue(vote.getRaidType().name())
                .difficulty(vote.getDifficulty().getDifficultyKor())
                .difficultyValue(vote.getDifficulty().name())
                .targetGateNumber(vote.getTargetGateNumber())
                .raidDatetime(vote.getRaidDatetime())
                .voteExpiresAt(vote.getVoteExpiresAt())
                .voteStatus(vote.getVoteStatus().name())
                .votedCharacters(vote.getParticipants().stream().map(VotedCharacterResponse::of).collect(Collectors.toList()))
                .build();
    }
}
