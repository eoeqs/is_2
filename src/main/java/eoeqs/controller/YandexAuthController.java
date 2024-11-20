package eoeqs.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/yandex")
public class YandexAuthController {

    private static final String YANDEX_USER_INFO_URL = "https://login.yandex.ru/info";

    @PostMapping
    public ResponseEntity<?> handleYandexToken(@RequestBody Map<String, String> tokenRequest) {
        String accessToken = tokenRequest.get("token");

        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Токен не предоставлен.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "OAuth " + accessToken);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(YANDEX_USER_INFO_URL, requestEntity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Ошибка при запросе к Яндекс API: " + response.getBody());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при запросе к Яндекс API: " + e.getMessage());
        }
    }
}