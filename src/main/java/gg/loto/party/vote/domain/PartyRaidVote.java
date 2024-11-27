package gg.loto.party.vote.domain;

import gg.loto.character.domain.Characters;
import gg.loto.global.entity.BaseEntity;
import gg.loto.party.domain.Party;
import gg.loto.party.vote.web.dto.VoteUpdateRequest;
import gg.loto.raid.entity.Difficulty;
import gg.loto.raid.entity.RaidType;
import gg.loto.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "party_raid_votes")
@NoArgsConstructor
public class PartyRaidVote extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User creator;

    @Column(nullable = true)
    private String name;

    @Column(name = "raid_datetime", nullable = false)
    private LocalDateTime raidDatetime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RaidType raidType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @Column(name = "target_gate_number", nullable = false)
    private int targetGateNumber;

    @Column(name = "vote_expires_at", nullable = false)
    private LocalDateTime voteExpiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteStatus voteStatus;

    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyRaidVoteParticipant> participants = new ArrayList<>();

    @Builder
    public PartyRaidVote(Party party, User creator, String name,
                         RaidType raidType, Difficulty difficulty, int targetGateNumber,
                         LocalDateTime raidDatetime, LocalDateTime voteExpiresAt, VoteStatus voteStatus){
        this.party = party;
        this.creator = creator;
        this.name = name;
        this.raidType = raidType;
        this.difficulty = difficulty;
        this.targetGateNumber = targetGateNumber;
        this.raidDatetime = raidDatetime;
        this.voteExpiresAt = voteExpiresAt;
        this.voteStatus = voteStatus;
    }

    public void addParticipant(Characters character){
        PartyRaidVoteParticipant participant = PartyRaidVoteParticipant.builder()
                .vote(this)
                .character(character)
                .build();
        this.participants.add(participant);
    }

    public void removeParticipant(Characters character){
        this.participants.removeIf(participant -> participant.getCharacter().equals(character));
    }

    public void update(VoteUpdateRequest dto) {
        this.name = dto.getName();
        this.raidType = RaidType.valueOf(dto.getRaidType());
        this.difficulty = Difficulty.valueOf(dto.getDifficulty());
        this.targetGateNumber = dto.getTargetGateNumber();
        this.raidDatetime = dto.getRaidDatetime();
        this.voteExpiresAt = dto.getVoteExpiresAt();
    }

    public boolean isCreator(User user) {
        return this.creator.equals(user);
    }

    public void cancel() {
        this.voteStatus = VoteStatus.CANCEL;
    }
}
