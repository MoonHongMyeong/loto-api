package gg.loto.party.web;

import gg.loto.global.auth.LoginUser;
import gg.loto.global.auth.dto.SessionUser;
import gg.loto.party.service.PartyInviteCodeService;
import gg.loto.party.service.PartyService;
import gg.loto.party.web.dto.InviteCodeResponse;
import gg.loto.party.web.dto.PartyInviteCodeCreateRequest;
import gg.loto.party.web.dto.PartyResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invite-code")
@RequiredArgsConstructor
public class InviteCodeApiController {
    private final PartyInviteCodeService inviteCodeService;
    @PostMapping
    public ResponseEntity<InviteCodeResponse> createInviteCode(
            @LoginUser SessionUser user,
            @Valid @RequestBody PartyInviteCodeCreateRequest dto)
    {
        return ResponseEntity.ok(inviteCodeService.createInviteCode(user, dto));
    }

    @GetMapping("/{code}")
    public ResponseEntity<PartyResponse> getPartyByInviteCode(
            @LoginUser SessionUser user,
            @PathVariable String code)
    {
        return ResponseEntity.ok(inviteCodeService.getPartyByInviteCode(code));
    }
}