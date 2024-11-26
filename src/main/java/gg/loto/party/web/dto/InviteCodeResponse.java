package gg.loto.party.web.dto;

import gg.loto.party.domain.PartyInviteCodes;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class InviteCodeResponse {
    private UUID code;
    private LocalDateTime expiresAt;

    @Builder
    public InviteCodeResponse(UUID code, LocalDateTime expiresAt){
        this.code = code;
        this.expiresAt = expiresAt;
    }

    public static InviteCodeResponse of (PartyInviteCodes inviteCodes){
        return InviteCodeResponse.builder()
                .code(inviteCodes.getCode())
                .expiresAt(inviteCodes.getExpiresAt())
                .build();
    }
}
