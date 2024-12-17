package gg.loto.auth.service;

import gg.loto.auth.domain.Token;
import gg.loto.auth.repository.TokenRepository;
import gg.loto.auth.web.dto.JwtTokenRequest;
import gg.loto.global.auth.exception.TokenException;
import gg.loto.global.auth.provider.JwtTokenProvider;
import gg.loto.global.exception.EntityNotFoundException;
import gg.loto.global.exception.ErrorCode;
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
            return UserResponse.from(token);
        }

        if (!jwtTokenProvider.validateToken(token.getAccessToken())) {
            throw new TokenException(token.getAccessToken(), ErrorCode.INVALID_TOKEN);
        }

        return UserResponse.from(token);
    }

    public void logout(JwtTokenRequest request) {
        Token token = tokenRepository.findByAccessToken(request.getAccessToken())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TOKEN_NOT_FOUND));

        tokenRepository.delete(token);
    }

    public String refreshToken(JwtTokenRequest request) {
        Token token = tokenRepository.findByAccessToken(request.getAccessToken())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TOKEN_NOT_FOUND));

        if (token.isRefreshTokenExpired()){
            throw new TokenException(token.getRefreshToken(), ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        if (!jwtTokenProvider.validateToken(token.getRefreshToken())) {
            throw new TokenException(token.getRefreshToken(), ErrorCode.INVALID_TOKEN);
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(token.getUser());
        token.updateAccessToken(newAccessToken, LocalDateTime.now().plusMinutes(10));

        return newAccessToken;
    }
}
