package be.labil.anacarde.presentation.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import be.labil.anacarde.domain.dto.db.StoreDetailDto;
import be.labil.anacarde.domain.model.*;
import be.labil.anacarde.infrastructure.persistence.CityRepository;
import be.labil.anacarde.infrastructure.persistence.LanguageRepository;
import be.labil.anacarde.infrastructure.persistence.RegionRepository;
import be.labil.anacarde.infrastructure.persistence.user.UserRepository;
import be.labil.anacarde.infrastructure.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityIntegrationTest {

	private @Autowired MockMvc mockMvc;
	private @Autowired ObjectMapper objectMapper;
	private @Autowired LanguageRepository languageRepository;
	private @Autowired UserRepository userRepository;
	private @Autowired JwtUtil jwtUtil;

	private Cookie jwtCookie;
	private User user;
	@Autowired
	private RegionRepository regionRepository;
	@Autowired
	private CityRepository cityRepository;

	private RequestPostProcessor addJwtCookie() {
		return request -> {
			request.setCookies(jwtCookie);
			return request;
		};
	}

	@BeforeEach
	public void setUpDatabase() {
		// Création de la langue
		Language language = Language.builder().code("fr").name("Français").build();
		language = languageRepository.save(language);
		Region region = Region.builder().name("sud").id(1).build();
		region = regionRepository.save(region);
		City city = City.builder().name("sud city").id(1).region(region).build();
		city = cityRepository.save(city);
		Address address = Address.builder().street("Rue de la paix").city(city).region(region)
				.build();

		user = Admin.builder().firstName("John").lastName("Doe").email("user@example.com")
				.address(address).password("password").registrationDate(LocalDateTime.now())
				.phone("+2290197000000").language(language).enabled(true).build();
		user = userRepository.save(user);
		String token = jwtUtil.generateToken(user);
		jwtCookie = new Cookie("jwt", token);
		jwtCookie.setHttpOnly(true);
		jwtCookie.setPath("/");
	}

	@AfterEach
	public void tearDownDatabase() {
		userRepository.deleteAll();
		languageRepository.deleteAll();
	}

	/**
	 *
	 * Vérifie que les points de terminaison protégés nécessitent un JWT.
	 */
	/**
	 * Vérifie que les points de terminaison protégés nécessitent un JWT, même si le token CSRF est
	 * présent.
	 */
	@Test
	public void testProtectedEndpointsWithoutJwt() throws Exception {
		Integer userId = 1;

		// GET → pas de CSRF requis, et pas de JWT → 401
		mockMvc.perform(get("/api/users/" + userId).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());

		// PUT → on ajoute csrf() pour ne pas tomber en 403 CSRF
		mockMvc.perform(put("/api/users/" + userId).with(csrf())
				.contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isUnauthorized());

		// DELETE → idem
		mockMvc.perform(delete("/api/users/" + userId).with(csrf()))
				.andExpect(status().isUnauthorized());

		// POST /roles
		mockMvc.perform(post("/api/users/" + userId + "/roles/ROLE_USER").with(csrf()))
				.andExpect(status().isUnauthorized());

		// PUT /roles
		mockMvc.perform(put("/api/users/" + userId + "/roles").with(csrf())
				.contentType(MediaType.APPLICATION_JSON).content("[]"))
				.andExpect(status().isUnauthorized());
	}

	/**
	 * Vérifie qu'une requête POST sans token CSRF renvoie 403 Forbidden.
	 */
	@Test
	public void testCreateUserWithoutCsrfReturnsForbidden() throws Exception {

		StoreDetailDto newStore = new StoreDetailDto();
		newStore.setLocation("POINT(2.3522 48.8566)");
		newStore.setUserId(user.getId());

		ObjectNode node = objectMapper.valueToTree(newStore);
		String jsonContent = node.toString();

		mockMvc.perform(post("/api/stores").contentType(MediaType.APPLICATION_JSON)
				.content(jsonContent).with(addJwtCookie())).andExpect(status().isForbidden());
	}
}
