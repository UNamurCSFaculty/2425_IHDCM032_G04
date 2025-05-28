package be.labil.anacarde.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class AdminUserApiControllerTest extends AbstractIntegrationTest {

	/**
	 * Teste la récupération d'un utilisa teur existant.
	 *
	 */
	@Test
	public void testGetUser() throws Exception {
		mockMvc.perform(get("/api/admin/users/" + getMainTestUser().getId())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value(getMainTestUser().getEmail()));
	}

	/**
	 * Teste la récupération de la liste de tous les utilisateurs.
	 *
	 */
	@Test
	public void testListUsers() throws Exception {
		mockMvc.perform(get("/api/admin/users").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
	}

	/**
	 * Teste la récupération d'un utilisateur inexistant.
	 *
	 */
	@Test
	public void testGetUserNotFound() throws Exception {
		mockMvc.perform(get("/api/admin/users/999999")).andExpect(status().isNotFound());
	}
}
