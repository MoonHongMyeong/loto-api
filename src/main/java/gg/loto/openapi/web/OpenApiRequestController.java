package gg.loto.openapi.web;

import gg.loto.global.auth.LoginUser;
import gg.loto.openapi.dto.CharacterOpenApiRequest;
import gg.loto.openapi.dto.CharacterOpenApiResponse;
import gg.loto.openapi.service.OpenApiRequestService;
import gg.loto.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/openapi")
@Profile("api")
@RequiredArgsConstructor
public class OpenApiRequestController {
    private final OpenApiRequestService openApiRequestService;

    @PostMapping("/character")
    public ResponseEntity<CharacterOpenApiResponse> requestCharacterProfiles(@LoginUser User user, @Valid @RequestBody CharacterOpenApiRequest dto){
        return ResponseEntity.ok(openApiRequestService.getCharacterProfiles(dto, user));
    }
}
