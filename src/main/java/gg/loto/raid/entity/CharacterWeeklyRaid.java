package gg.loto.raid.entity;

import gg.loto.character.domain.Characters;
import gg.loto.global.entity.BaseEntity;
import gg.loto.raid.web.dto.RaidUpdateRequest;
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

    public void update(RaidUpdateRequest dto){
        Difficulty newDifficulty;
        try {
            newDifficulty = Difficulty.valueOf(dto.getDifficulty().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 난이도입니다: " + dto.getDifficulty());
        }

        if (!this.raidType.canEnterWithDifficulty(newDifficulty)) {
            throw new IllegalArgumentException(
                    String.format("해당 레이드(%s)는 %s 난이도를 지원하지 않습니다.",
                            this.raidType, newDifficulty));
        }

        if (!this.raidType.isValidStage(dto.getStage())) {
            throw new IllegalArgumentException(
                    String.format("유효하지 않은 관문 번호입니다. 레이드: %s, 관문: %d",
                            this.raidType, dto.getStage()));
        }

        int requiredLevel = this.raidType.getRequiredItemLevel(newDifficulty);
        if (Integer.parseInt(this.character.getItemMaxLevel()) < requiredLevel) {
            throw new IllegalArgumentException(
                    String.format("아이템 레벨이 부족합니다. 필요 레벨: %d", requiredLevel));
        }

        boolean isDuplicate = this.character.getWeeklyRaids().stream()
                .filter(raid -> !raid.getId().equals(this.id))  // 자기 자신 제외
                .anyMatch(raid ->
                        raid.getRaidType() == this.raidType &&
                                raid.getDifficulty() == newDifficulty &&
                                raid.getStage() == dto.getStage()
                );

        if (isDuplicate) {
            throw new IllegalStateException("이미 체크한 레이드입니다.");
        }

        // 모든 검증을 통과한 경우에만 업데이트
        this.difficulty = newDifficulty;
        this.stage = dto.getStage();
    }
}
