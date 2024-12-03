package eoeqs.config;

import com.nimbusds.jwt.JWTClaimsSetTransformer;
import eoeqs.repository.OAuthUserRepository;
import eoeqs.repository.RoleRepository;
import eoeqs.util.DefaultOAuthGrantedAuthoritiesAccessor;
import eoeqs.util.NimbusSpringJwtClaimsSetTransformer;
import eoeqs.util.YandexOpaqueTokenIntrospector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.client.RestClient;


@Configuration
@EnableWebSecurity
public class SecurityConfig {



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/ws/**").permitAll()
                        .requestMatchers( "/api/cities/**").hasAnyRole("USER", "ADMIN")
                                .anyRequest().authenticated()
                )
                .exceptionHandling(ehc -> ehc.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .oauth2ResourceServer(rsc -> rsc.opaqueToken(Customizer.withDefaults()));


        return http.build();
    }



    @Bean
    public RestClient yandexUserInfoIntrospectionClient(@Value("${spring.security.oauth2.resourceserver.opaquetoken.introspection-uri}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }


    @Bean
    public OpaqueTokenIntrospector yandexOpaqueTokenIntrospector(
            RestClient userInfoClient, RoleRepository roleRepository, OAuthUserRepository userRepository) {
        JWTClaimsSetTransformer<JwtClaimsSet> claimsSetTransformer = new NimbusSpringJwtClaimsSetTransformer();
        DefaultOAuthGrantedAuthoritiesAccessor authoritiesAccessor = new DefaultOAuthGrantedAuthoritiesAccessor(
                roleRepository, userRepository);
        return new YandexOpaqueTokenIntrospector(userInfoClient, claimsSetTransformer, authoritiesAccessor);
    }
}
