package gg.loto.character.web;

import gg.loto.character.service.CharactersService;
import gg.loto.character.web.dto.CharacterListResponse;
import gg.loto.character.web.dto.CharacterSaveRequest;
import gg.loto.global.auth.LoginUser;
import gg.loto.global.auth.dto.SessionUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/characters")
@RequiredArgsConstructor
public class CharactersApiController {

    private final CharactersService charactersService;

    @PostMapping
    public ResponseEntity<List<CharacterListResponse>> createCharacter(@LoginUser SessionUser user, @Valid @RequestBody CharacterSaveRequest dto){
        return ResponseEntity.ok(charactersService.createCharacter(user, dto));
    }
}
