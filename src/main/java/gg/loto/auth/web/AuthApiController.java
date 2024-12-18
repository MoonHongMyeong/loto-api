package gg.loto.auth.web;

import gg.loto.auth.service.AuthService;
import gg.loto.auth.web.dto.AuthResponse;
import gg.loto.auth.web.dto.TokenRefreshResponse;
import gg.loto.auth.web.dto.TokenResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final AuthService authService;

    @PostMapping("/discord/login")
    public ResponseEntity<AuthResponse> discordLogin(String code, HttpServletResponse response) {
        TokenResponse tokenResponse = authService.loginWithDiscord(code);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/auth")
                .maxAge(Duration.ofDays(365))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(AuthResponse.from(tokenResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> jwtLogin(@RequestHeader("Authorization") String token) {
        TokenResponse tokenResponse = authService.loginWithJwt(token.replace("bearer ", ""));
        return ResponseEntity.ok(AuthResponse.from(tokenResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal Long userId) {
        authService.logout(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@CookieValue("refreshToken") String refreshToken) {
        TokenResponse tokenResponse = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(new TokenRefreshResponse(tokenResponse.getAccessToken()));
    }
}
