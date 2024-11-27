package gg.loto.party.vote.domain;

import gg.loto.character.domain.Characters;
import gg.loto.global.entity.BaseEntity;
import gg.loto.party.domain.Party;
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

    @Column(name = "raid_datetime")
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
    public PartyRaidVote(Party party, User creator, Characters creatorCharacter,
                         RaidType raidType, Difficulty difficulty,
                         int targetGateNumber, LocalDateTime raidDatetime, LocalDateTime voteExpiresAt){

        validatePartyMembership(party, creatorCharacter);
        validateRaidRequirements(raidType, difficulty, targetGateNumber, creatorCharacter);
        this.party = party;
        this.creator = creator;
        this.raidType = raidType;
        this.difficulty = difficulty;
        this.targetGateNumber = targetGateNumber;
        this.raidDatetime = raidDatetime;
        this.voteExpiresAt = voteExpiresAt;

        addParticipant(creatorCharacter);
    }

    private void validateRaidRequirements(RaidType raidType, Difficulty difficulty, int targetGateNumber, Characters character) {
        int requiredLevel = raidType.getRequiredItemLevelForStage(difficulty, targetGateNumber);
        if (Integer.parseInt(character.getItemMaxLevel()) < requiredLevel) {
            throw new IllegalArgumentException(String.format("아이템 레벨이 부족합니다. 필요 레벨: %d"+ requiredLevel));
        }
    }

    private void validatePartyMembership(Party party, Characters creatorCharacter) {
        if (!party.isPartyMember(creatorCharacter.getUser())){
            throw new IllegalArgumentException("파티 구성원만 투표를 생성할 수 있습니다.");
        }
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
}
