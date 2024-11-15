package gg.loto.character.domain;

import gg.loto.global.entity.BaseEntity;
import gg.loto.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "characters")
public class Characters extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "class_name", nullable = false)
    private String className;

    @Column(name = "item_level", nullable = false)
    private float itemLevel;
    
    @Column(name = "character_image")
    private String characterImage;
}