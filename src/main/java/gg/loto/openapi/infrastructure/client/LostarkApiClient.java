package gg.loto.openapi.infrastructure.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import gg.loto.openapi.dto.CharacterOpenApiResponse;
import lombok.RequiredArgsConstructor;

@Component
@Profile("api")
@RequiredArgsConstructor
public class LostarkApiClient {
    @Value("${lostark.url}")
    private String lostarkApiUrl;
    
    private final RestTemplate restTemplate;
    
    public CharacterOpenApiResponse fetchCharacterProfile(String characterName, String apiKey) {
        HttpHeaders headers = createHeaders(apiKey);
        Map<String, Object> responseBody;

        try {
            HttpEntity<String> requestEntity = new HttpEntity(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    lostarkApiUrl + "/armories/characters/" + characterName + "/profiles",
                    HttpMethod.GET,
                    requestEntity,
                    Map.class
            );

            responseBody = response.getBody();

        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException("캐릭터를 찾을 수 없습니다.");
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new IllegalArgumentException("API 키가 유효하지 않습니다.");
        } catch (HttpClientErrorException.Forbidden e) {
            throw new IllegalArgumentException("API 키가 유효하지 않습니다.");
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("API 요청 오류가 발생했습니다.");
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("로스트아크 서버 오류가 발생했습니다.");
        } catch (Exception e) {
            throw new RuntimeException("API 요청 오류가 발생했습니다.");
        }

        if ( responseBody == null ) {
            throw new IllegalArgumentException("캐릭터를 찾을 수 없습니다.");
        }

        return convertToCharacterOpenapiResponse(responseBody);
    }

    private static CharacterOpenApiResponse convertToCharacterOpenapiResponse(Map<String, Object> responseBody) {
        return CharacterOpenApiResponse.builder()
                .ServerName((String) responseBody.get("ServerName"))
                .CharacterName((String) responseBody.get("CharacterName"))
                .CharacterLevel((Integer) responseBody.get("CharacterLevel"))
                .CharacterClassName((String) responseBody.get("CharacterClassName"))
                .ItemAvgLevel((String) responseBody.get("ItemAvgLevel"))
                .ItemMaxLevel((String) responseBody.get("ItemMaxLevel"))
                .CharacterImage((String) responseBody.get("CharacterImage"))
                .build();
    }

    private HttpHeaders createHeaders(String apiKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/json");
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");
        return headers;
    }
}