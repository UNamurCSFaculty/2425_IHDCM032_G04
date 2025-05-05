package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.model.Admin;
import be.labil.anacarde.domain.model.Language;
import be.labil.anacarde.domain.model.User;
import be.labil.anacarde.infrastructure.persistence.LanguageRepository;
import be.labil.anacarde.infrastructure.persistence.user.UserRepository;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
	@Autowired
	private LanguageRepository languageRepository;

	@BeforeEach
	public void setUpDatabase() {
		// Création de la langue
		Language language = Language.builder().code("fr").name("Français").build();
		language = languageRepository.save(language);

		// Création de l'utilisateur admin
		User user = new Admin();
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setEmail("user@example.com");
		user.setPhone("+2290197020000");
		user.setPassword(passwordEncoder.encode("password"));
		user.setRegistrationDate(LocalDateTime.now());
		user.setEnabled(true);
		user.setLanguage(language);

		userRepository.save(user);
	}

	@AfterEach
	public void tearDownDatabase() {
		userRepository.deleteAll();
		languageRepository.deleteAll();
	}

	/**
	 * Teste l'authentification réussie d'un utilisateur et la structure du DTO retourné.
	 */
	@Test
	public void testAuthenticateUserIntegration_Success() throws Exception {
		// On récupère la langue pour l'ID attendu
		Language lang = languageRepository.findAll().getFirst();

		String requestBody = "{\"username\": \"user@example.com\", \"password\": \"password\"}";

		mockMvc.perform(post("/api/auth/signin").contentType("application/json").content(requestBody))
				.andExpect(status().isOk()).andExpect(cookie().exists("jwt"))
				// le corps est un JSON
				.andExpect(content().contentType("application/json"))
				// vérification des champs du DTO
				.andExpect(jsonPath("$.type").value("admin")).andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.firstName").value("John")).andExpect(jsonPath("$.lastName").value("Doe"))
				.andExpect(jsonPath("$.email").value("user@example.com"))
				.andExpect(jsonPath("$.registrationDate").exists()).andExpect(jsonPath("$.validationDate").isEmpty())
				.andExpect(jsonPath("$.enabled").value(true)).andExpect(jsonPath("$.address").isEmpty())
				.andExpect(jsonPath("$.phone").value("+2290197020000")).andExpect(jsonPath("$.roles").isArray())
				.andExpect(jsonPath("$.roles", hasSize(0))).andExpect(jsonPath("$.language.id").value(lang.getId()))
				.andExpect(jsonPath("$.language.name").value("Français"));
	}

	/**
	 * Teste l'échec de l'authentification d'un utilisateur.
	 */
	@Test
	public void testAuthenticateUserIntegration_Failure() throws Exception {
		String requestBody = "{\"username\": \"user@example.com\", \"password\": \"wrongpassword\"}";

		mockMvc.perform(post("/api/auth/signin").contentType("application/json").content(requestBody))
				.andExpect(status().isUnauthorized());
	}

	/**
	 * Teste la gestion d'une requête d'authentification avec un corps de requête vide.
	 */
	@Test
	public void testAuthenticateUserIntegration_NullRequestBody() throws Exception {
		String requestBody = "";

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

	/**
	 * Teste l'accès à /me avec un cookie JWT valide.
	 */
	@Test
	public void testGetCurrentUser_Success() throws Exception {
		// 1) Authentifier pour récupérer le cookie
		String loginBody = "{\"username\": \"user@example.com\", \"password\": \"password\"}";
		MvcResult result = mockMvc.perform(post("/api/auth/signin").contentType("application/json").content(loginBody))
				.andExpect(status().isOk()).andReturn();

		Cookie jwt = result.getResponse().getCookie("jwt");
		Language lang = languageRepository.findAll().getFirst();

		// 2) Appeler /me avec le cookie
		mockMvc.perform(get("/api/auth/me").cookie(jwt)).andExpect(status().isOk())
				.andExpect(content().contentType("application/json")).andExpect(jsonPath("$.type").value("admin"))
				.andExpect(jsonPath("$.email").value("user@example.com"))
				.andExpect(jsonPath("$.firstName").value("John"))
				.andExpect(jsonPath("$.language.id").value(lang.getId()));
	}

	/**
	 * Teste l'accès à /me sans cookie (non authentifié).
	 */
	@Test
	public void testGetCurrentUser_Unauthorized() throws Exception {
		mockMvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
	}

	/**
	 * Teste la déconnexion : le cookie "jwt" devient vide et Max-Age=0.
	 */
	@Test
	public void testLogout_ClearsJwtCookie() throws Exception {
		// 1) Authentification pour obtenir le cookie JWT
		String loginBody = "{\"username\": \"user@example.com\", \"password\": \"password\"}";
		MvcResult loginResult = mockMvc
				.perform(post("/api/auth/signin").contentType("application/json").content(loginBody))
				.andExpect(status().isOk()).andReturn();

		Cookie jwt = loginResult.getResponse().getCookie("jwt");
		assertNotNull(jwt, "Le cookie jwt doit exister après signin");

		// 2) Appel de /signout avec le cookie
		mockMvc.perform(post("/api/auth/signout").cookie(jwt)).andExpect(status().isOk())
				// on retrouve bien un cookie "jwt" expiré
				.andExpect(cookie().exists("jwt")).andExpect(cookie().value("jwt", "")) // valeur vidée
				.andExpect(cookie().maxAge("jwt", 0)); // expiring immediately
	}
}
