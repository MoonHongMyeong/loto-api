package gg.loto.character.web;

import gg.loto.character.service.CharactersService;
import gg.loto.character.web.dto.CharacterListResponse;
import gg.loto.character.web.dto.CharacterResponse;
import gg.loto.character.web.dto.CharacterSaveRequest;
import gg.loto.character.web.dto.CharacterUpdateRequest;
import gg.loto.global.auth.LoginUser;
import gg.loto.global.auth.dto.SessionUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/characters")
@RequiredArgsConstructor
public class CharactersApiController {

    private final CharactersService charactersService;

    @PostMapping
    public ResponseEntity<CharacterResponse> createCharacter(@LoginUser SessionUser user, @Valid @RequestBody CharacterSaveRequest dto){
        return ResponseEntity.ok(charactersService.createCharacter(user, dto));
    }

    @PutMapping("/{characterId}")
    public ResponseEntity<CharacterResponse> updateCharacter(@LoginUser SessionUser user, @PathVariable Long characterId, @Valid @RequestBody CharacterUpdateRequest dto){
        return ResponseEntity.ok(charactersService.updateCharacter(user, characterId, dto));
    }

    @DeleteMapping("/{characterId}")
    public ResponseEntity<Void> deleteCharacter(@LoginUser SessionUser user, @PathVariable Long characterId){
        charactersService.deleteCharacter(user, characterId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CharacterListResponse>> getUserCharacters(@LoginUser SessionUser user){
        return ResponseEntity.ok(charactersService.getUserCharacters(user));
    }

    @GetMapping("/{characterId}")
    public ResponseEntity<CharacterResponse> getCharacter(@LoginUser SessionUser user, @PathVariable Long characterId){
        return ResponseEntity.ok(charactersService.getUserCharacter(user, characterId));
    }
}
