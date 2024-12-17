package gg.loto.openapi.infrastructure.client;

import java.util.Map;

import gg.loto.global.exception.ErrorCode;
import gg.loto.openapi.exception.OpenApiException;
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
            throw new OpenApiException(ErrorCode.CHARACTER_NOT_FOUND);
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new OpenApiException(ErrorCode.INVALID_API_KEY);
        } catch (HttpClientErrorException.Forbidden e) {
            throw new OpenApiException(ErrorCode.INVALID_API_KEY);
        } catch (HttpClientErrorException e) {
            throw new OpenApiException(ErrorCode.API_REQUEST_ERROR);
        } catch (HttpServerErrorException e) {
            throw new OpenApiException(ErrorCode.LOSTARK_SERVER_ERROR);
        } catch (Exception e) {
            throw new OpenApiException(ErrorCode.API_REQUEST_ERROR);
        }

        if ( responseBody == null ) {
            throw new OpenApiException(ErrorCode.CHARACTER_NOT_FOUND);
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