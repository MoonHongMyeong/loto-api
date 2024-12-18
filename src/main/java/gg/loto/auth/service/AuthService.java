package gg.loto.auth.service;

import gg.loto.auth.web.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final DiscordOAuth2Service discordOAuth2Service;
    private final JwtLoginService jwtLoginService;
    
    public TokenResponse loginWithDiscord(String code) {
        return discordOAuth2Service.login(code);
    }
    
    public TokenResponse loginWithJwt(String token) {
        return jwtLoginService.login(token);
    }
    
    public void logout(Long userId) {
        jwtLoginService.logout(userId);
    }

    public TokenResponse refreshToken(String token) {
        return jwtLoginService.refreshToken(token);
    }
} 