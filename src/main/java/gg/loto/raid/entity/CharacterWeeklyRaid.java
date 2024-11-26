package gg.loto.raid.entity;

import gg.loto.character.domain.Characters;
import gg.loto.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "character_weekly_raid")
@NoArgsConstructor
public class CharacterWeeklyRaid extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private Characters character;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RaidType raidType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @Column(nullable = false)
    private int stage;

    @Builder
    public CharacterWeeklyRaid(Characters character, RaidType raidType, Difficulty difficulty, int stage){
        if ( !raidType.isValidStage(stage) ){
            throw new IllegalArgumentException("유효하지 않은 관문입니다.");
        }
        this.character = character;
        this.raidType = raidType;
        this.difficulty = difficulty;
        this.stage = stage;
    }
}
