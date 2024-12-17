package gg.loto.auth.service;

import gg.loto.auth.web.dto.JwtTokenRequest;
import gg.loto.user.web.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final DiscordOAuth2Service discordOAuth2Service;
    private final JwtLoginService jwtLoginService;
    
    public UserResponse loginWithDiscord(String code) {
        return discordOAuth2Service.login(code);
    }
    
    public UserResponse loginWithJwt(JwtTokenRequest request) {
        return jwtLoginService.login(request);
    }
    
    public void logout(JwtTokenRequest request) {
        jwtLoginService.logout(request);
    }

    public String refreshToken(JwtTokenRequest request) {
        return jwtLoginService.refreshToken(request);
    }
} 