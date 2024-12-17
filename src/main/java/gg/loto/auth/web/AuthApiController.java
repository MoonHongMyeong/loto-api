package gg.loto.auth.web;

import gg.loto.auth.service.AuthService;
import gg.loto.auth.web.dto.JwtTokenRequest;
import gg.loto.user.web.dto.UserResponse;
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

    private final AuthService authService;

    @PostMapping("/discord/login")
    public ResponseEntity<UserResponse> discordLogin(String code) {
        return ResponseEntity.ok(authService.loginWithDiscord(code));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> jwtLogin(@RequestBody JwtTokenRequest request) {
        return ResponseEntity.ok(authService.loginWithJwt(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody JwtTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<UserResponse> refreshToken(@RequestBody JwtTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
}
