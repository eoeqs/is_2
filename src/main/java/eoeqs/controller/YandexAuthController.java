package eoeqs.controller;


import eoeqs.exception.UnauthorizedAccessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class YandexAuthController {

    @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-secret}")
    private String clientSecret;

    @GetMapping("/public")
    public String test(Authentication user) {
        return user.getName();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/secure")
    public String secure() {
        return "Allowed to secure";
    }

    @PostMapping("/logout")
    public ResponseEntity<?> revokeYandexToken(Authentication authentication, @RequestBody Map<String, String> requestBody) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        String accessToken = requestBody.get("access_token");


        try {
            String body = String.format(
                    "access_token=%s&client_id=%s&client_secret=%s",
                    accessToken, clientId, clientSecret
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<String> request = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://oauth.yandex.ru/revoke_token",
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok("Token successfully revoked");
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body("Failed to revoke token: " + response.getBody());
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while revoking token: " + e.getMessage());
        }
    }



}
