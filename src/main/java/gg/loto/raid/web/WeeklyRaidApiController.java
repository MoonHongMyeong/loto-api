package gg.loto.raid.web;

import gg.loto.character.web.dto.CharacterListResponse;
import gg.loto.global.auth.LoginUser;
import gg.loto.raid.service.RaidService;
import gg.loto.raid.web.dto.RaidSaveRequest;
import gg.loto.raid.web.dto.RaidUpdateRequest;
import gg.loto.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/characters/{characterId}/weeklyRaids")
@RequiredArgsConstructor
public class WeeklyRaidApiController {
    private final RaidService raidService;

    @PostMapping
    public ResponseEntity<CharacterListResponse> saveWeeklyRaid(@LoginUser User user, @PathVariable(name = "characterId") Long characterId, @Valid @RequestBody RaidSaveRequest dto){
        return ResponseEntity.ok(raidService.saveWeeklyRaid(user, characterId, dto));
    }

    @PutMapping("/{raidId}")
    public ResponseEntity<CharacterListResponse> updateWeeklyRaid(@LoginUser User user, @PathVariable(name = "characterId") Long characterId, @PathVariable(name = "raidId") Long raidId, @Valid @RequestBody RaidUpdateRequest dto) {
        return ResponseEntity.ok(raidService.updateWeeklyRaid(user, characterId, raidId, dto));
    }

    @DeleteMapping("/{raidId}")
    public ResponseEntity<CharacterListResponse> removeWeeklyRaid(@LoginUser User user, @PathVariable(name = "characterId") Long characterId, @PathVariable(name = "raidId") Long raidId){
        return ResponseEntity.ok(raidService.removeWeeklyRaid(user, characterId, raidId));
    }
}
