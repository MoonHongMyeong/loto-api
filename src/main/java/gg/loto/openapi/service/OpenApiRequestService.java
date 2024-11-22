package gg.loto.openapi.service;

import gg.loto.global.auth.dto.SessionUser;
import gg.loto.openapi.dto.CharacterOpenApiRequest;
import gg.loto.openapi.dto.CharacterOpenApiResponse;
import gg.loto.openapi.infrastructure.client.LostarkApiClient;
import gg.loto.user.domain.User;
import gg.loto.user.service.UserFindDao;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenApiRequestService {
    @Value("${lostark.api-key}")
    private final String LOSTARK_API_KEY;

    private final LostarkApiClient lostarkApiClient;
    private final UserFindDao userFindDao;

    public CharacterOpenApiResponse getCharacterProfiles(CharacterOpenApiRequest dto, SessionUser sessionUser) {
        User user = userFindDao.getCurrentUser(sessionUser);

        String apiKey = determineApiKey(user);

        return lostarkApiClient.fetchCharacterProfile(dto.getCharacterName(), apiKey);
    }

    private String determineApiKey(User user) {
        return user.getApiKey() == null ? LOSTARK_API_KEY : user.getApiKey();
    }
}
