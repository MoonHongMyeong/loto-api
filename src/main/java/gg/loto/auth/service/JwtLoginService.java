package gg.loto.auth.service;

import gg.loto.auth.domain.Token;
import gg.loto.auth.repository.TokenRepository;
import gg.loto.auth.web.dto.TokenResponse;
import gg.loto.global.auth.exception.TokenException;
import gg.loto.global.auth.provider.JwtTokenProvider;
import gg.loto.global.exception.EntityNotFoundException;
import gg.loto.global.exception.ErrorCode;
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
    public TokenResponse login(String requestAccessToken) {
        Token token = tokenRepository.findByAccessToken(requestAccessToken)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TOKEN_NOT_FOUND));

        if (token.isAccessTokenExpired()) {
            if (token.isRefreshTokenExpired()) {
                throw new TokenException(token.getRefreshToken(), ErrorCode.EXPIRED_REFRESH_TOKEN);
            }

            if (!jwtTokenProvider.validateToken(token.getRefreshToken())) {
                throw new TokenException(token.getRefreshToken(), ErrorCode.INVALID_TOKEN);
            }

            String newAccessToken = jwtTokenProvider.generateAccessToken(token.getUser());
            LocalDateTime newExpiresAt = LocalDateTime.now().plusMinutes(10);

            token.updateAccessToken(newAccessToken, newExpiresAt);
            return TokenResponse.from(token);
        }

        if (!jwtTokenProvider.validateToken(token.getAccessToken())) {
            throw new TokenException(token.getAccessToken(), ErrorCode.INVALID_TOKEN);
        }

        return TokenResponse.from(token);
    }

    public void logout(Long userId) {
        Token token = tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TOKEN_NOT_FOUND));

        tokenRepository.delete(token);
    }

    public TokenResponse refreshToken(String requestRefreshToken) {
        Token token = tokenRepository.findByRefreshToken(requestRefreshToken)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TOKEN_NOT_FOUND));

        if (token.isRefreshTokenExpired()){
            tokenRepository.delete(token);
            throw new TokenException(token.getRefreshToken(), ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        if (!jwtTokenProvider.validateToken(token.getRefreshToken())) {
            tokenRepository.delete(token);
            throw new TokenException(token.getRefreshToken(), ErrorCode.INVALID_TOKEN);
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(token.getUser());
        token.updateAccessToken(newAccessToken, LocalDateTime.now().plusMinutes(10));

        return TokenResponse.from(token);
    }
}
