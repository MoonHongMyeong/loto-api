package gg.loto.party.web;

import gg.loto.global.auth.LoginUser;
import gg.loto.global.auth.dto.SessionUser;
import gg.loto.party.service.PartyService;
import gg.loto.party.web.dto.PartyResponse;
import gg.loto.party.web.dto.PartySaveRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/party")
@RequiredArgsConstructor
public class PartyApiController {
    private final PartyService partyService;

    @PostMapping
    public ResponseEntity<PartyResponse> createParty(@LoginUser SessionUser user, @Valid @RequestBody PartySaveRequest dto){
        return ResponseEntity.ok(partyService.createParty(user, dto));
    }
}
