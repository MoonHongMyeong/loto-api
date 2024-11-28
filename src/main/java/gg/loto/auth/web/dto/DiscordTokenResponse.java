package gg.loto.auth.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DiscordTokenResponse {
    @JsonProperty("access_token")
    @NotNull
    private String accessToken;

    @JsonProperty("token_type")
    @NotNull
    private String tokenType;

    @JsonProperty("expires_in")
    @NotNull
    private Integer expiresIn;

    @JsonProperty("refresh_token")
    @NotNull
    private String refreshToken;

    @JsonProperty("scope")
    @NotNull
    private String scope;
}
