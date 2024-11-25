package eoeqs.auth;//package eoeqs.auth;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//
//import java.security.Key;
//import java.util.Date;
//import java.util.Map;
//import java.util.function.Function;
//
//@Service
//public class JwtService {
//    @Value("09d62fc50a9d8c09c3c3a82a3d5762cdfd85de199cb864b5378a77ff2c230159")
//    private String secretKey;
//
//    @Value("3600000")
//    private long jwtExpiration;
//
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    //    public String generateToken(UserDetails userDetails) {
////        return generateToken(new HashMap<>(), userDetails);
////    }
//    public String generateToken(Map<String, Object> claims, String subject) {
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(subject)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
//                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
//        return buildToken(extraClaims, userDetails, jwtExpiration);
//    }
//
//    public long getExpirationTime() {
//        return jwtExpiration;
//    }
//
//    private String buildToken(
//            Map<String, Object> extraClaims,
//            UserDetails userDetails,
//            long expiration
//    ) {
//        return Jwts
//                .builder()
//                .setClaims(extraClaims)
//                .setSubject(userDetails.getUsername())
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + expiration))
//                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    public boolean isTokenValid(String token, UserDetails userDetails) {
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
//    }
//
//    private boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    private Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
//
//    private Claims extractAllClaims(String token) {
//        return Jwts
//                .parserBuilder()
//                .setSigningKey(getSignInKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    private Key getSignInKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//}

import eoeqs.dto.ResponseForUserProfileDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
public class JwtService {

    private static final SecretKey key = Jwts.SIG.HS256.key().build();


    public String generateToken(ResponseForUserProfileDto token) {
        return Jwts.builder().subject(token.userEmail()).signWith(key)
                .claim("profile_picture", token.userProfilePicture())
                .claim("id", token.userId())
                .claim("roles", token.userRole())
                .compact();
    }

    public Claims getClaims(String jwt) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
    }
}
