package gg.loto.auth.service;

import gg.loto.auth.domain.Token;
import gg.loto.auth.repository.TokenRepository;
import gg.loto.auth.web.dto.JwtTokenRequest;
import gg.loto.global.auth.provider.JwtTokenProvider;
import gg.loto.user.web.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JwtLoginService {
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;

    @Transactional
    public UserResponse login(JwtTokenRequest requestToken) {
        Token token = tokenRepository.findByAccessToken(requestToken.getAccessToken())
                .orElseThrow(() -> new RuntimeException("토큰 정보가 존재하지 않습니다."));

        if (token.getAccessTokenExpiresAt().isBefore(LocalDateTime.now())) {
            if (token.getRefreshTokenExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("리프레시 토큰이 만료되었습니다. 다시 로그인해주세요.");
            }

            if (!jwtTokenProvider.validateToken(token.getRefreshToken())) {
                throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
            }

            String newAccessToken = jwtTokenProvider.generateAccessToken(token.getUser());
            LocalDateTime newExpiresAt = LocalDateTime.now().plusMinutes(10);

            token.updateAccessToken(newAccessToken, newExpiresAt);
            return UserResponse.from(token);
        }

        if (!jwtTokenProvider.validateToken(token.getAccessToken())) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        return UserResponse.from(token);
    }

    public void logout(JwtTokenRequest request) {
        Token token = tokenRepository.findByAccessToken(request.getAccessToken())
                .orElseThrow(() -> new RuntimeException("토큰 정보가 존재하지 않습니다"));

        tokenRepository.delete(token);
    }
}
