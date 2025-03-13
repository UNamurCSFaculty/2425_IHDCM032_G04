package be.labil.anacarde.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
/**
 * This class provides methods to generate, parse, and validate JSON Web Tokens (JWT) for user
 * authentication.
 */
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.token.validity.hours}")
    /** Token validity duration in hours. */
    private long tokenValidityHours;

    /**
     * Converts the secret key from Base64 encoding to a SecretKey object.
     *
     * @return The SecretKey generated from the decoded secret key.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts all claims from the provided JWT token.
     *
     * @param token The JWT token from which to extract claims.
     * @return The Claims object containing all claims present in the token.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts a specific claim from the JWT token using the provided resolver function.
     *
     * @param token The JWT token from which to extract the claim.
     * @param claimsResolver A function that specifies which claim to extract from the Claims.
     * @return The value of the extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Retrieves the username (subject) from the JWT token.
     *
     * @param token The JWT token from which to retrieve the username.
     * @return The username extracted from the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Retrieves the expiration date from the JWT token.
     *
     * @param token The JWT token from which to retrieve the expiration date.
     * @return The expiration Date of the token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Checks whether the JWT token has expired.
     *
     * @param token The JWT token to check for expiration.
     * @return True if the token has expired, false otherwise.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generates a JWT token for the given user details.
     *
     * @param userDetails The UserDetails object containing user information.
     * @return A signed JWT token as a String.
     */
    public String generateToken(UserDetails userDetails) {
        return createToken(Map.of(), userDetails.getUsername());
    }

    /**
     * Creates a JWT token with custom claims and a subject.
     *
     * @param customClaims A map of custom claims to include in the token.
     * @param subject The subject (typically the username) for which the token is generated.
     * @return A signed JWT token as a String.
     */
    private String createToken(Map<String, Object> customClaims, String subject) {
        Date now = new Date();
        long validityInMillis = tokenValidityHours * 60 * 60 * 1000;
        Date expiryDate = new Date(now.getTime() + validityInMillis);

        JwtBuilder builder =
                Jwts.builder()
                        .claims(customClaims)
                        .subject(subject)
                        .issuedAt(now)
                        .expiration(expiryDate)
                        .signWith(getSigningKey());
        return builder.compact();
    }

    /**
     * Validates the provided JWT token against the given user details.
     *
     * @param token The JWT token to validate.
     * @param userDetails The UserDetails object to validate against.
     * @return True if the token is valid and matches the user details, false otherwise.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (ExpiredJwtException e) {
            return false;
        }
    }
}
