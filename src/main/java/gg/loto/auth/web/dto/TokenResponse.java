package gg.loto.auth.web.dto;

import gg.loto.auth.domain.Token;
import gg.loto.user.web.dto.UserResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private UserResponse user;

    @Builder
    TokenResponse(String accessToken, String refreshToken, UserResponse userResponse){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = userResponse;
    }

    public static TokenResponse from (Token token){
        return TokenResponse.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .userResponse(UserResponse.from(token.getUser()))
                .build();
    }
}
