package eoeqs.controller;

import eoeqs.auth.JwtService;
import eoeqs.dto.ResponseForUserProfileDto;
import eoeqs.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class YandexAuthController {
    private final JwtService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(YandexAuthController.class);

    public YandexAuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/yandex")
    public ResponseEntity<?> authenticateYandex(@RequestBody Map<String, String> payload) {
        logger.info("Получен запрос на авторизацию через Яндекс.");

        String yandexToken = payload.get("token");
        if (yandexToken == null || yandexToken.isBlank()) {
            logger.warn("Ошибка: токен Яндекса отсутствует или пуст.");
            return ResponseEntity.badRequest().body("Yandex token is missing");
        }

        try {
            String username = validateYandexToken(yandexToken);

            logger.info("Токен Яндекса успешно валиден. Генерируем JWT для пользователя: {}", username);

            var userInfo = new ResponseForUserProfileDto("yandex-id", username, "example@yandex.ru", null, "USER");
            String jwtToken = jwtService.generateToken(userInfo);

            logger.info("JWT успешно сгенерирован: {}", jwtToken);

            return ResponseEntity.ok(Map.of("jwtToken", jwtToken));
        } catch (Exception e) {
            logger.error("Ошибка валидации токена Яндекса: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Yandex token");
        }
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(
            @AuthenticationPrincipal OAuth2User principal,
            OAuth2AuthenticationToken authentication) {
        logger.info("Запрос текущего пользователя через OAuth2.");

        if (principal == null || authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Пользователь не авторизован.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }
        User user = (User) authentication.getPrincipal();
        ResponseForUserProfileDto response = new ResponseForUserProfileDto(
                user.getId().toString(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImageUrl()
        );
        return ResponseEntity.ok(response);
        return userService.getTokenForSecurity(principal, authentication, jwtService);
    }

    private String validateYandexToken(String token) {
        logger.debug("Начинаем валидацию токена Яндекса: {}", token);
        // Реальный вызов API Яндекса должен быть здесь
        if ("valid-yandex-token".equals(token)) {
            return "yandexUser";
        }
        throw new IllegalArgumentException("Токен Яндекса недействителен.");
    }
}
