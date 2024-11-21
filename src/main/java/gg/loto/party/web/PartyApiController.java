package gg.loto.party.web;

import gg.loto.global.auth.LoginUser;
import gg.loto.global.auth.dto.SessionUser;
import gg.loto.party.service.PartyService;
import gg.loto.party.web.dto.PartyResponse;
import gg.loto.party.web.dto.PartySaveRequest;
import gg.loto.party.web.dto.PartyUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
