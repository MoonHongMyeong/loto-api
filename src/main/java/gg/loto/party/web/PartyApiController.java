package gg.loto.party.web;

import gg.loto.global.auth.LoginUser;
import gg.loto.global.auth.dto.SessionUser;
import gg.loto.party.service.PartyService;
import gg.loto.party.web.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/party")
@RequiredArgsConstructor
public class PartyApiController {
    private final PartyService partyService;

    @PostMapping
    public ResponseEntity<PartyResponse> createParty(@LoginUser SessionUser user, @Valid @RequestBody PartySaveRequest dto){
        return ResponseEntity.ok(partyService.createParty(user, dto));
    }

    @PutMapping("/{partyId}")
    public ResponseEntity<PartyResponse> updateParty(@LoginUser SessionUser user, @PathVariable(name = "partyId") Long partyId, @Valid @RequestBody PartyUpdateRequest dto){
        return ResponseEntity.ok(partyService.updateParty(user, partyId, dto));
    }

    @PutMapping("/{partyId}/leader/{userId}")
    public ResponseEntity<PartyResponse> transferLeadership(@LoginUser SessionUser user, @PathVariable(name="partyId") Long partyId, @PathVariable(name="userId") Long userId){
        return ResponseEntity.ok(partyService.transferLeadership(user, partyId, userId));
    }

    @PostMapping("/{partyId}/members")
    public ResponseEntity<PartyResponse> joinParty(@LoginUser SessionUser user, @PathVariable(name="partyId") Long partyId, @Valid @RequestBody PartyMemberRequest dto){
        return ResponseEntity.ok(partyService.joinParty(user, partyId, dto));
    }

    @DeleteMapping("/{partyId}/members")
    public ResponseEntity leaveParty(@LoginUser SessionUser user, @PathVariable(name = "partyId") Long partyId, @Valid @RequestBody PartyMemberRequest dto){
        partyService.leaveParty(user, partyId, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{partyId}/members/{userId}")
    public ResponseEntity kickMember(@LoginUser SessionUser user, @PathVariable(name = "partyId") Long partyId, @PathVariable(name = "userId") Long userId){
        partyService.kickMember(user, partyId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{partyId}")
    public ResponseEntity removeParty(@LoginUser SessionUser user, @PathVariable(name = "partyId") Long partyId){
        partyService.removeParty(user, partyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<PartyListResponse>> getMyParties(@LoginUser SessionUser user){
        return ResponseEntity.ok(partyService.getMyParties(user));
    }

    @GetMapping("/{partyId}")
    public ResponseEntity<PartyResponse> getParty(@LoginUser SessionUser user, @PathVariable(name = "partyId") Long partyId){
        return ResponseEntity.ok(partyService.getParty(user, partyId));
    }

    @GetMapping("/{partyId}/characters")
    public ResponseEntity<PartyMemberCharactersResponse> getPartyMemberCharacters(@LoginUser SessionUser user, @PathVariable(name = "partyId") Long partyId, @RequestParam(name = "lastCharacterId", required = false) Long lastCharacterId, @RequestParam(name = "isMobile", defaultValue = "true") boolean isMobile){
        return ResponseEntity.ok(partyService.getPartyMemberCharacters(user, partyId, lastCharacterId, isMobile));
    }
}
