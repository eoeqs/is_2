package eoeqs.controller;


import eoeqs.jwt.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class YandexAuthController {
    private final JwtService jwtService;

    public YandexAuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/test")
    public void test(@AuthenticationPrincipal OAuth2User user) {
        log.info("pupu " + user.getAttributes());
    }


    @PostMapping("/yandex")
    public ResponseEntity<?> authenticateYandex(@RequestBody Map<String, String> payload) {
        log.info("Получен запрос на авторизацию через Яндекс.");

        String yandexToken = payload.get("token");
        log.info("Получен запрос на авторизацию через Яндекс. Токен: {}", yandexToken);

        if (yandexToken == null || yandexToken.isBlank()) {
            log.warn("Ошибка: токен Яндекса отсутствует или пуст.");
            return ResponseEntity.badRequest().body("Yandex token is missing");
        }

        try {
            log.info("Проверяем токен Яндекса...");
            String username = validateYandexToken(yandexToken);

            log.info("Токен Яндекса успешно валиден. Генерируем JWT для пользователя: {}", username);
            String jwtToken = jwtService.generateToken(Map.of("username", username), username);

            log.info("JWT успешно сгенерирован: {}", jwtToken);

            return ResponseEntity.ok(Map.of("jwtToken", jwtToken));
        } catch (Exception e) {
            log.error("Ошибка валидации токена Яндекса: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Yandex token");
        }
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        log.info("Запрос текущего пользователя. Проверяем контекст безопасности...");
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Пользователь не авторизован.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }

        String username = authentication.getName();
        log.info("Авторизован пользователь: {}", username);

        String jwt = jwtService.generateToken(Map.of("username", username), username);
        log.info("Сгенерирован JWT для текущего пользователя: {}", jwt);

        return ResponseEntity.ok(Map.of("token", jwt));
    }

    private String validateYandexToken(String token) {
        log.debug("Начинаем валидацию токена Яндекса: {}", token);
        if ("valid-yandex-token".equals(token)) {
            log.debug("Токен валиден. Возвращаем пользователя.");
            return "yandexUser";
        }
        throw new IllegalArgumentException("Токен Яндекса недействителен.");
    }
}
