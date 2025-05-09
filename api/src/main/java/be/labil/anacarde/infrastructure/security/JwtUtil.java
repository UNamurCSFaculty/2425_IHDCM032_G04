package be.labil.anacarde.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
/**
 * Cette classe fournit des méthodes pour générer, analyser et valider des JSON Web Tokens (JWT)
 * destinés à l'authentification des utilisateurs.
 */
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.token.validity.months}")
	private long tokenValidityMonths;

	/**
	 * Convertit la clé secrète encodée en Base64 en un objet SecretKey.
	 *
	 * @return La SecretKey générée à partir de la clé secrète décodée.
	 */
	private SecretKey getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	/**
	 * Extrait l'ensemble des claims présents dans le token JWT fourni.
	 *
	 * @param token
	 *            Le token JWT dont il faut extraire les claims.
	 * @return L'objet Claims contenant toutes les informations du token.
	 */
	public Claims extractAllClaims(String token) {
		return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token)
				.getPayload();
	}

	/**
	 * Extrait une claim spécifique du token JWT à l'aide de la fonction résolveur fournie.
	 *
	 * @param token
	 *            Le token JWT dont il faut extraire la claim.
	 * @param claimsResolver
	 *            Une fonction précisant la claim à extraire depuis l'objet Claims.
	 * @param <T>
	 *            Le type de la claim à extraire.
	 * @return La valeur de la claim extraite.
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * Récupère le nom d'utilisateur (subject) depuis le token JWT.
	 *
	 * @param token
	 *            Le token JWT dont il faut extraire le nom d'utilisateur.
	 * @return Le nom d'utilisateur extrait du token.
	 */
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	/**
	 * Récupère la date d'expiration du token JWT.
	 *
	 * @param token
	 *            Le token JWT dont il faut extraire la date d'expiration.
	 * @return La date d'expiration du token.
	 */
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	/**
	 * Vérifie si le token JWT a expiré.
	 *
	 * @param token
	 *            Le token JWT à vérifier.
	 * @return true si le token a expiré, false sinon.
	 */
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	/**
	 * Génère un token JWT pour l'utilisateur spécifié.
	 *
	 * @param userDetails
	 *            L'objet UserDetails contenant les informations de l'utilisateur.
	 * @return Un token JWT signé sous forme de String.
	 */
	public String generateToken(UserDetails userDetails) {
		return createToken(Map.of(), userDetails.getUsername());
	}

	/**
	 * Crée un token JWT en incluant des claims personnalisées et un subject.
	 *
	 * @param customClaims
	 *            Une map des claims personnalisées à inclure dans le token.
	 * @param subject
	 *            Le subject (généralement le nom d'utilisateur) pour lequel le token est généré.
	 * @return Un token JWT signé sous forme de String.
	 */
	private String createToken(Map<String, Object> customClaims, String subject) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expiry = now.plusMonths(tokenValidityMonths);

		ZoneId zone = ZoneId.systemDefault();
		Date issuedAt = Date.from(now.atZone(zone).toInstant());
		Date expiration = Date.from(expiry.atZone(zone).toInstant());

		JwtBuilder builder = Jwts.builder().claims(customClaims).subject(subject).issuedAt(issuedAt)
				.expiration(expiration).signWith(getSigningKey());
		return builder.compact();
	}

	/**
	 * Valide le token JWT fourni en le comparant aux informations de l'utilisateur.
	 *
	 * @param token
	 *            Le token JWT à valider.
	 * @param userDetails
	 *            L'objet UserDetails avec lequel valider le token.
	 * @return true si le token est valide et correspond aux informations de l'utilisateur, false
	 *         sinon.
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
