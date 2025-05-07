package be.labil.anacarde.infrastructure.security;

import static org.junit.jupiter.api.Assertions.*;

import be.labil.anacarde.domain.model.Admin;
import be.labil.anacarde.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Teste les fonctionnalités de génération, d'extraction et de validation des tokens JWT par la classe JwtUtil.
 */
public class JwtUtilTest {

	private JwtUtil jwtUtil;

	@BeforeEach
	public void setUp() {
		jwtUtil = new JwtUtil();
		// Injecte les valeurs de configuration pour les besoins des tests via ReflectionTestUtils
		ReflectionTestUtils.setField(jwtUtil, "secretKey", "V2Vha1NlY3VyZUtleVNlY3VyZUtleVNlY3VyZUtleVNlY3VyZQ==");
		ReflectionTestUtils.setField(jwtUtil, "tokenValidityMonths", 1);
	}

	/**
	 * Teste la génération et la validation d'un token JWT pour un utilisateur.
	 *
	 * <p>
	 * Ce test crée un utilisateur fictif sans autorités, génère un token JWT pour cet utilisateur, puis vérifie que le
	 * nom d'utilisateur extrait du token correspond à celui de l'utilisateur et que le token est valide.
	 */
	@Test
	public void testGenerateAndValidateToken() {
		// Crée un utilisateur fictif sans autorités
		User user = new Admin();
		user.setEmail("myUser");
		user.setPassword("password");

		String token = jwtUtil.generateToken(user);
		assertNotNull(token, "Le token ne doit pas être nul");

		String extractedUsername = jwtUtil.extractUsername(token);
		assertEquals(user.getUsername(), extractedUsername,
				"Le nom d'utilisateur extrait doit correspondre à celui de l'utilisateur");

		boolean isTokenValid = jwtUtil.validateToken(token, user);
		assertTrue(isTokenValid, "Le token doit être valide");
	}

	/**
	 * Teste l'expiration d'un token JWT.
	 *
	 * <p>
	 * Pour ce test, la durée de validité du token est définie à 0 heure (expiration immédiate). Le test attend ensuite
	 * brièvement pour s'assurer que le token est expiré, puis vérifie que la validation du token échoue.
	 *
	 * @throws InterruptedException
	 *             en cas d'interruption pendant l'attente
	 */
	@Test
	public void testTokenExpiration() throws InterruptedException {
		// Pour tester l'expiration, on définit la validité du token à 0 heure (expiration
		// immédiate)
		ReflectionTestUtils.setField(jwtUtil, "tokenValidityMonths", 0L);
		User user = new Admin();
		user.setEmail("myUser");
		user.setPassword("password");

		String token = jwtUtil.generateToken(user);

		// Attend un court instant pour s'assurer que le token expire
		Thread.sleep(2);

		boolean isTokenValid = jwtUtil.validateToken(token, user);
		assertFalse(isTokenValid, "Le token doit être expiré");
	}
}
