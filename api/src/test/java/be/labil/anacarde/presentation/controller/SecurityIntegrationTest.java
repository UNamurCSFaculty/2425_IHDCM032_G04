package be.labil.anacarde.presentation.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import be.labil.anacarde.domain.dto.CooperativeDto;
import be.labil.anacarde.domain.dto.user.ProducerDetailDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
public class SecurityIntegrationTest extends AbstractIntegrationTest {

	private @Autowired MockMvc mockMvc;
	private @Autowired ObjectMapper objectMapper;

	private RequestPostProcessor jwtCookie() {
		return request -> {
			request.setCookies(getJwtCookie());
			return request;
		};
	}

	/**
	 *
	 * Vérifie que les points de terminaison protégés nécessitent un JWT.
	 */
	/**
	 * Vérifie que les points de terminaison protégés nécessitent un JWT, même si le token CSRF est présent.
	 */
	@Test
	public void testProtectedEndpointsWithoutJwt() throws Exception {
		Integer userId = getMainTestUser().getId();

		// GET → pas de CSRF requis, et pas de JWT → 401
		mockMvc.perform(get("/api/users/" + userId).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());

		// PUT → on ajoute csrf() pour ne pas tomber en 403 CSRF
		mockMvc.perform(put("/api/users/" + userId).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isUnauthorized());

		// DELETE → idem
		mockMvc.perform(delete("/api/users/" + userId).with(csrf())).andExpect(status().isUnauthorized());

		// POST /roles
		mockMvc.perform(post("/api/users/" + userId + "/roles/ROLE_USER").with(csrf()))
				.andExpect(status().isUnauthorized());

		// PUT /roles
		mockMvc.perform(put("/api/users/" + userId + "/roles").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content("[]")).andExpect(status().isUnauthorized());
	}

	/**
	 * Vérifie qu'une requête POST sans token CSRF renvoie 403 Forbidden.
	 */
	@Test
	public void testCreateUserWithoutCsrfReturnsForbidden() throws Exception {
		CooperativeDto cooperativeDto = new CooperativeDto();
		cooperativeDto.setId(getMainTestCooperative().getId());

		ProducerDetailDto newUser = new ProducerDetailDto();
		newUser.setFirstName("Bob");
		newUser.setLastName("NoCsrf");
		newUser.setEmail("no.csrf@example.com");
		newUser.setPassword("pwd");
		newUser.setLanguage(getMainLanguageDto());

		ObjectNode node = objectMapper.valueToTree(newUser);
		node.put("password", newUser.getPassword());
		String jsonContent = node.toString();

		mockMvc.perform(
				post("/api/users").contentType(MediaType.APPLICATION_JSON).content(jsonContent).with(jwtCookie()))
				.andExpect(status().isForbidden());
	}
}
