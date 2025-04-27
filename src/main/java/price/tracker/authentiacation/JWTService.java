package price.tracker.authentiacation;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JWTService {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String issueJWT(int id, String username) {
        long now = System.currentTimeMillis();
        long validity = 1000L * 60 * 60 * 24;

        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("username", username)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + validity))
                .signWith(key)
                .compact();
    }

    public Claims validateJWT(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT");
        }
    }
}
