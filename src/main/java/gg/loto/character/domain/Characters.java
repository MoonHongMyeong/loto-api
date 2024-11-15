package gg.loto.character.domain;

import gg.loto.character.web.dto.CharacterUpdateRequest;
import gg.loto.global.entity.BaseEntity;
import gg.loto.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}