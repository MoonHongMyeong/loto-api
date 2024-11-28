package gg.loto.auth.web;

import gg.loto.auth.service.LoginService;
import gg.loto.auth.web.dto.DiscordTokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final LoginService loginService;

    @PostMapping("/discord/login")
    public void login(@Valid @RequestBody DiscordTokenResponse token) {
        loginService.login(token);
    }   

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        loginService.logout();
        return ResponseEntity.ok().build();
    }
}
