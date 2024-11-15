package gg.loto.openapi.infrastructure.client;

import gg.loto.global.config.RestTemplateConfig;
import gg.loto.openapi.dto.CharacterOpenApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RestClientTest(LostarkApiClient.class)
@Import(RestTemplateConfig.class)
@ActiveProfiles("test")
public class LostarkApiClientTest {
    @Autowired
    private LostarkApiClient apiClient;

    @Value("${lostark.api-key}")
    private String apiKey;

    @Test
    @DisplayName("실제 로스트아크 API 호출 테스트")
    void fetchCharacterProfileTest() {
        // given
        String characterName = "타이탈로스의하수인";

        // when
        CharacterOpenApiResponse response = apiClient.fetchCharacterProfile(characterName, apiKey);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getCharacterName()).isEqualTo(characterName);
        assertThat(response.getServerName()).isNotNull();
        assertThat(response.getCharacterLevel()).isGreaterThan(0);
    }

    @Test
    @DisplayName("존재하지 않는 캐릭터 조회시 예외 발생")
    void fetchNonExistentCharacterTest() {
        // given 설마 생기지는 않겠지..?
        String nonExistentCharacter = "타이탈로스의하수인인인";

        // when & then
        assertThatThrownBy(() ->
                apiClient.fetchCharacterProfile(nonExistentCharacter, apiKey)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("캐릭터를 찾을 수 없습니다.");
    }
}
