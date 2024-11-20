package gg.loto.party.domain;

import gg.loto.global.entity.BaseEntity;
import gg.loto.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "parties")
@NoArgsConstructor
public class Party extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "limit", nullable = false)
    private int limit;

    @Column(name = "party_type", nullable = false)
    private PartyType partyType;

    @Builder
    public Party(User user, String name, int limit, PartyType partyType){
        this.user = user;
        this.name = name;
        this.limit = limit;
        this.partyType = partyType;
    }
}
