package eoeqs.util;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSetTransformer;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;

public class NimbusSpringJwtClaimsSetTransformer implements JWTClaimsSetTransformer<JwtClaimsSet> {

    @Override
    public JwtClaimsSet transform(JWTClaimsSet oldClaimsSet) {
        return JwtClaimsSet.builder()
                .id(oldClaimsSet.getJWTID())
                .issuer(oldClaimsSet.getIssuer())
                .claim("login", oldClaimsSet.getClaim("login"))
                .claim("email", oldClaimsSet.getClaim("email"))
                .claim("name", oldClaimsSet.getClaim("name"))
                .claim("display_name", oldClaimsSet.getClaim("display_name"))
                .claim("psuid", oldClaimsSet.getClaim("psuid"))
                .claim("uid", oldClaimsSet.getClaim("uid"))
                .expiresAt(oldClaimsSet.getExpirationTime().toInstant())
                .issuedAt(oldClaimsSet.getIssueTime().toInstant())
                .build();
    }
}
