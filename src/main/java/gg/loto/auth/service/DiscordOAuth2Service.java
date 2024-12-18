package gg.loto.auth.service;

import gg.loto.auth.domain.Token;
import gg.loto.auth.repository.TokenRepository;
import gg.loto.auth.web.dto.DiscordTokenResponse;
import gg.loto.auth.web.dto.DiscordUserInfo;
import gg.loto.auth.web.dto.TokenResponse;
import gg.loto.global.auth.provider.JwtTokenProvider;
import gg.loto.user.domain.User;
import gg.loto.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiscordOAuth2Service{

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final RestTemplate restTemplate;

    @Value("${discord.client-id}")
    private String clientId;
    @Value("${discord.client-secret}")
    private String clientSecret;
    @Value("${discord.redirect-uri}")
    private String redirectUri;

    @Transactional
    public TokenResponse login(String requestCode) {
        DiscordTokenResponse discordToken = getDiscordToken(requestCode);
        DiscordUserInfo userInfo = getDiscordUserInfo(discordToken.getAccessToken());

        User user = userRepository.findByDiscordId(userInfo.getId())
                .orElseGet(() -> createUser(userInfo));

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        Optional<Token> token = tokenRepository.findByUser(user);

        token.ifPresent(t -> t.updateAccessToken(accessToken, LocalDateTime.now().plusMinutes(10)));
        token.orElseGet(() -> tokenRepository.save(Token.builder()
                .user(user)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresAt(LocalDateTime.now().plusMinutes(10))
                .refreshTokenExpiresAt(LocalDateTime.now().plusYears(1))
                .build()));

        return TokenResponse.from(token.get());
    }

    @Transactional
    private User createUser(DiscordUserInfo userInfo) {
        User user = User.builder()
                .discordId(userInfo.getId())
                .discordUsername(userInfo.getUsername())
                .discordAvatar(userInfo.getAvatarUrl())
                .nickname(userInfo.getUsername())
                .build();

        return userRepository.save(user);
    }

    private DiscordTokenResponse getDiscordToken(String requestCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("grant_type", "authorization_code");
        body.add("code", requestCode);
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        return restTemplate.postForEntity(
                "https://discord.com/api/oauth2/token",
                request,
                DiscordTokenResponse.class
        ).getBody();
    }

    private DiscordUserInfo getDiscordUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        return restTemplate.exchange(
                "https://discord.com/api/users/@me",
                HttpMethod.GET,
                entity,
                DiscordUserInfo.class
        ).getBody();
    }

}
