package eoeqs.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class YandexAuthController {


    @GetMapping("/public")
    public String test(Authentication user) {
        return user.getName();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/secure")
    public String secure() {
        return "Allowed to secure";
    }




}
