package eoeqs.config;

import eoeqs.service.CustomAuthenticationProvider;
import eoeqs.service.UserDetailsServiceImpl;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import java.nio.charset.StandardCharsets;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers("/api/ws/**").permitAll()
//                        .requestMatchers("/api/users/register", "/api/users/login").permitAll()
                                .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers( "/api/cities/**").hasAnyAuthority("USER", "ADMIN")
//                                .requestMatchers("/city-actions/**").authenticated()
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(o -> o.jwt(j -> j.decoder(jwtDecoder)))
//                .oauth2Login(oauth2 -> oauth2
//                        .loginPage("/api/login")
//                        .defaultSuccessUrl("/city-actions")
//                        .failureUrl("/login?error=true")
//                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();
    }


    @Bean
    public AuthenticationManager authManager(HttpSecurity http, CustomAuthenticationProvider authenticationProvider) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider).userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public JwtDecoder jwtDecoder(@Value("${spring.security.oauth2.client.registration.yandex.client-secret}") String value){
        return NimbusJwtDecoder.withSecretKey(Keys.hmacShaKeyFor(value.getBytes(StandardCharsets.UTF_8))).build();
    }
}
