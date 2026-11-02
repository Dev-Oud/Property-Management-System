package property.management.propex.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final String encodedSecret;
    private final long accessTokenTtlMillis;

    public JwtService(
            @Value("${jwt.secret}") String encodedSecret,
            @Value("${jwt.expiration:900000}") long accessTokenTtlMillis) {
        this.encodedSecret = encodedSecret;
        this.accessTokenTtlMillis = accessTokenTtlMillis;
        if (accessTokenTtlMillis < 60_000 || accessTokenTtlMillis > 3_600_000) {
            throw new IllegalArgumentException("JWT access-token lifetime must be between 1 minute and 1 hour");
        }
        signingKey(); // Fail at startup when the required key is absent or too short.
    }

    public String generateAccessToken(UserDetails userDetails, UUID tokenId) {
        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + accessTokenTtlMillis);
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .id(tokenId.toString())
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(signingKey())
                .compact();
    }

    public Claims parseClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public UUID extractTokenId(String token) {
        return UUID.fromString(parseClaims(token).getId());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            Claims claims = parseClaims(token);
            return userDetails.getUsername().equals(claims.getSubject())
                    && claims.getId() != null
                    && claims.getExpiration() != null
                    && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public long getAccessTokenTtlMillis() {
        return accessTokenTtlMillis;
    }

    private SecretKey signingKey() {
        byte[] keyBytes = Decoders.BASE64.decode(encodedSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
