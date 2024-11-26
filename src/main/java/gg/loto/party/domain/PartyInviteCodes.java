package gg.loto.party.domain;

import gg.loto.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "party_invite_codes")
@NoArgsConstructor
public class PartyInviteCodes extends BaseEntity {

    @Id
    @Column(name = "code", nullable = false)
    private UUID code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Builder
    public PartyInviteCodes(UUID code, Party party, LocalDateTime expiresAt) {
        this.code = code;
        this.party = party;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return this.expiresAt.isBefore(LocalDateTime.now());
    }
}