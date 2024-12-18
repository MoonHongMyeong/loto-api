package gg.loto.auth.web.dto;

import gg.loto.user.web.dto.UserResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private UserResponse user;

    @Builder
    AuthResponse(String accessToken, UserResponse user){
        this.accessToken = accessToken;
        this.user = user;
    }

    public static AuthResponse from (TokenResponse tokenResponse){
        return AuthResponse.builder()
                .accessToken(tokenResponse.getAccessToken())
                .user(tokenResponse.getUser())
                .build();
    }
}
