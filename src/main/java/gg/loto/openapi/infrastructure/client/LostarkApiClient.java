package gg.loto.openapi.infrastructure.client;

import gg.loto.openapi.dto.CharacterOpenApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class LostarkApiClient {
    @Value("${lostark.url}")
    private final String LOSTARK_API_URL;
    
    private final RestTemplate restTemplate;
    
    public CharacterOpenApiResponse fetchCharacterProfile(String characterName, String apiKey) {
        HttpHeaders headers = createHeaders(apiKey);
        
        try {
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<CharacterOpenApiResponse> response = restTemplate.exchange(
                    LOSTARK_API_URL + "/armories/characters/" + characterName + "/profiles",
                    HttpMethod.GET,
                    requestEntity,
                    CharacterOpenApiResponse.class
            );
            
            return response.getBody();
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
        }catch (Exception e) {
            throw new RuntimeException("API 요청 오류가 발생했습니다.");
        }
    }
    
    private HttpHeaders createHeaders(String apiKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/json");
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");
        return headers;
    }
}