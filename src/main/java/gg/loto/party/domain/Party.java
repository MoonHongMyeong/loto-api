package gg.loto.party.domain;

import gg.loto.character.domain.Characters;
import gg.loto.global.entity.BaseEntity;
import gg.loto.party.web.dto.PartyUpdateRequest;
import gg.loto.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "party_type", nullable = false)
    private PartyType partyType;

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyMember> members = new ArrayList<>();

    public void addMember(Characters character){
        PartyMember member = PartyMember.builder()
                .party(this)
                .character(character)
                .build();
        this.members.add(member);
    }

    public void removeMember(Characters character){
        this.members.removeIf(member -> member.getCharacter().equals(character));
    }

    @Builder
    public Party(User user, String name, int capacity, PartyType partyType){
        this.user = user;
        this.name = name;
        this.capacity = capacity;
        this.partyType = partyType;
    }

    public void update(PartyUpdateRequest dto){
        this.name = dto.getName();
        this.capacity = dto.getCapacity();
        this.partyType = PartyType.valueOf(dto.getPartyType());
    }

    public void transferLeadership(User newLeader) {
        this.user = newLeader;
    }
}
