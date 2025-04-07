
package be.labil.anacarde.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.model.User;
import be.labil.anacarde.infrastructure.persistence.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
/** Test d'intégration pour le contrôleur d'authentification. */
public class AuthenticationApiControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/** Prépare la base de données de test en créant un utilisateur. */
	@BeforeEach
	public void setUpDatabase() {
		userRepository.deleteAll();

		User user = new User();
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setEmail("user@example.com");
		user.setPassword(passwordEncoder.encode("password"));
		user.setRegistrationDate(LocalDateTime.now());
		user.setEnabled(true);

		userRepository.save(user);
	}

	/**
	 * Teste l'authentification réussie d'un utilisateur.
	 */
	@Test
	public void testAuthenticateUserIntegration_Success() throws Exception {
		String requestBody = "{\"username\": \"user@example.com\", \"password\": \"password\"}";

		mockMvc.perform(post("/api/auth/signin").contentType("application/json").content(requestBody))
				.andExpect(status().isOk()).andExpect(cookie().exists("jwt"))
				.andExpect(content().string("Utilisateur authentifié avec succès"));
	}

	/**
	 * Teste l'échec de l'authentification d'un utilisateur.
	 */
	@Test
	public void testAuthenticateUserIntegration_Failure() throws Exception {
		String requestBody = "{\"username\": \"user@example.com\", \"password\": \"wrongpassword\"}";

		mockMvc.perform(post("/api/auth/signin").contentType("application/json").content(requestBody))
				.andExpect(status().isUnauthorized()).andExpect(content().string("Échec de l'authentification"));
	}

	/**
	 * Teste la gestion d'une requête d'authentification avec un corps de requête nul.
	 */
	@Test
	public void testAuthenticateUserIntegration_NullRequestBody() throws Exception {
		String requestBody = ""; // Le corps n'est jamais null, mais peut être vide.

		mockMvc.perform(post("/api/auth/signin").contentType("application/json").content(requestBody))
				.andExpect(status().isBadRequest());
	}

	/**
	 * Teste la gestion d'une requête d'authentification sans mot de passe.
	 */
	@Test
	public void testAuthenticateUserIntegration_MissingPassword() throws Exception {
		String requestBody = "{\"username\": \"user@example.com\"}";

		mockMvc.perform(post("/api/auth/signin").contentType("application/json").content(requestBody))
				.andExpect(status().isBadRequest());
	}

	/**
	 * Teste la gestion d'une requête d'authentification sans nom d'utilisateur.
	 */
	@Test
	public void testAuthenticateUserIntegration_MissingUsername() throws Exception {
		String requestBody = "{\"password\": \"password\"}";

		mockMvc.perform(post("/api/auth/signin").contentType("application/json").content(requestBody))
				.andExpect(status().isBadRequest());
	}
}
