package gg.loto.openapi.service;

import gg.loto.openapi.dto.CharacterOpenApiRequest;
import gg.loto.openapi.dto.CharacterOpenApiResponse;
import gg.loto.openapi.infrastructure.client.LostarkApiClient;
import gg.loto.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("api")
@RequiredArgsConstructor
public class OpenApiRequestService {
    @Value("${lostark.api-key}")
    private String LOSTARK_API_KEY;

    private final LostarkApiClient lostarkApiClient;

    public CharacterOpenApiResponse getCharacterProfiles(CharacterOpenApiRequest dto, User user) {
        String apiKey = determineApiKey(user);

        return lostarkApiClient.fetchCharacterProfile(dto.getCharacterName(), apiKey);
    }

    private String determineApiKey(User user) {
        return user.getApiKey() == null ? LOSTARK_API_KEY : user.getApiKey();
    }
}
