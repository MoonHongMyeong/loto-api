package gg.loto.character.domain;

import gg.loto.character.web.dto.CharacterUpdateRequest;
import gg.loto.global.entity.BaseEntity;
import gg.loto.party.domain.PartyMember;
import gg.loto.raid.entity.CharacterWeeklyRaid;
import gg.loto.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "characters")
@NoArgsConstructor
public class Characters extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "server_name", nullable = false)
    private String serverName;

    @Column(name = "character_name", nullable = false, unique = true)
    private String characterName;

    @Column(name = "character_class_name", nullable = false)
    private String characterClassName;

    @Column(name = "item_max_level", nullable = false)
    private String itemMaxLevel;

    @Column(name = "item_avg_level", nullable = false)
    private String itemAvgLevel;

    @Column(name = "character_level", nullable = false)
    private int characterLevel;
    
    @Column(name = "character_image")
    private String characterImage;

    @OneToMany(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CharacterWeeklyRaid> weeklyRaids = new ArrayList<>();

    @Builder
    public Characters(User user, String characterName, String serverName, String characterClassName, String itemMaxLevel, String itemAvgLevel, int characterLevel, String characterImage) {
        this.user = user;
        this.characterName = characterName;
        this.serverName = serverName;
        this.characterClassName = characterClassName;
        this.itemMaxLevel = itemMaxLevel;
        this.itemAvgLevel = itemAvgLevel;
        this.characterLevel = characterLevel;
        this.characterImage = characterImage;
    }

    public void update(CharacterUpdateRequest dto) {
        this.characterName = dto.getCharacterName();
        this.characterClassName = dto.getCharacterClassName();
        this.characterImage = dto.getCharacterImage();
        this.characterLevel = dto.getCharacterLevel();
        this.itemMaxLevel = dto.getItemMaxLevel();
        this.itemAvgLevel = dto.getItemAvgLevel();
    }

    public boolean isOwnership(User user){
        return this.getUser().equals(user);
    }

    public void addWeeklyRaid(CharacterWeeklyRaid weeklyRaid) {
        boolean isDuplicate = weeklyRaids.stream()
                .anyMatch(existing ->
                    existing.getRaidType() == weeklyRaid.getRaidType() &&
                            existing.getDifficulty() == weeklyRaid.getDifficulty() &&
                            existing.getStage() == weeklyRaid.getStage()
                );
        if (isDuplicate) {
            throw new IllegalStateException("이미 체크된 레이드입니다.");
        }
        this.weeklyRaids.add(weeklyRaid);
    }

    public void removeWeeklyRaid(Long raidId) {
        CharacterWeeklyRaid weeklyRaid = weeklyRaids.stream()
                    .filter(raid -> raid.getId().equals(raidId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("삭제할 레이드가 존재하지 않습니다."));

        this.weeklyRaids.remove(weeklyRaid);
    }
}