package eoeqs.util;


import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSetTransformer;
import com.nimbusds.jwt.JWTParser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.web.client.RestClient;

import java.text.ParseException;
import java.util.Collection;

public class YandexOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

    private final RestClient userInfoClient;
    private final JWTClaimsSetTransformer<JwtClaimsSet> transformer;
    private final OAuthGrantedAuthoritiesAccessor authoritiesAccessor;

    public YandexOpaqueTokenIntrospector(
            RestClient userInfoClient,
            JWTClaimsSetTransformer<JwtClaimsSet> transformer,
            OAuthGrantedAuthoritiesAccessor authoritiesAccessor) {
        this.userInfoClient = userInfoClient;
        this.transformer = transformer;
        this.authoritiesAccessor = authoritiesAccessor;
    }

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        String userInfoToken = userInfoClient.get()
                .header("Authorization", "OAuth %s".formatted(token))
                .retrieve()
                .body(String.class);
        try {
            JWT parsedJwt = JWTParser.parse(userInfoToken == null ? "" : userInfoToken);
            Jwt.Builder jwtBuilder = Jwt.withTokenValue(userInfoToken)
                    .header("alg", parsedJwt.getHeader().getAlgorithm().getName());
            parsedJwt.getJWTClaimsSet().toType(transformer).getClaims().forEach(jwtBuilder::claim);
            Jwt jwt = jwtBuilder.build();

            Collection<GrantedAuthority> authorities = authoritiesAccessor.access(
                    jwt.getClaimAsString("login"), "yandex");

            return new OAuth2IntrospectionAuthenticatedPrincipal(
                    jwt.getClaim("login").toString(),
                    jwt.getClaims(),
                    authorities
            );
        } catch (ParseException e) {
            throw new JwtException("Failed to parse Jwt", e);
        }
    }
}
