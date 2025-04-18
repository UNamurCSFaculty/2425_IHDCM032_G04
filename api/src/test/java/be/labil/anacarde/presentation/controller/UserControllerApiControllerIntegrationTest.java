package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.LanguageDto;
import be.labil.anacarde.domain.dto.RoleDto;
import be.labil.anacarde.domain.dto.user.AdminDetailDto;
import be.labil.anacarde.domain.dto.user.ExporterDetailDto;
import be.labil.anacarde.domain.dto.user.UserDetailDto;
import be.labil.anacarde.domain.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Tests d'intégration pour le contrôleur des utilisateurs. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerApiControllerIntegrationTest extends AbstractIntegrationTest {

	private @Autowired MockMvc mockMvc;
	private @Autowired ObjectMapper objectMapper;
	private @Autowired BCryptPasswordEncoder bCryptPasswordEncoder;

	/**
	 * RequestPostProcessor qui ajoute automatiquement le cookie JWT à chaque requête.
	 *
	 * @return le RequestPostProcessor configuré.
	 */
	private RequestPostProcessor jwt() {
		return request -> {
			request.setCookies(getJwtCookie());
			return request;
		};
	}

	/**
	 * Teste la récupération d'un utilisateur existant.
	 * 
	 */
	@Test
	public void testGetUser() throws Exception {
		mockMvc.perform(get("/api/users/" + getMainTestUser().getId()).accept(MediaType.APPLICATION_JSON).with(jwt()))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(jsonPath("$.email").value(getMainTestUser().getEmail()));
	}

	/**
	 * Teste la création d'un nouvel utilisateur.
	 * 
	 */
	@Test
	public void testCreateUser() throws Exception {
		LanguageDto languageDto = new LanguageDto();
		languageDto.setId(getMainLanguage().getId());
		UserDetailDto newUser = new AdminDetailDto();
		newUser.setFirstName("Alice");
		newUser.setLastName("Smith");
		newUser.setEmail("alice.smith@example.com");
		newUser.setPassword("secret");
		newUser.setLanguage(languageDto);

		ObjectNode node = objectMapper.valueToTree(newUser);
		node.put("password", newUser.getPassword()); // Ajout manuel car le mot de passe n'est pas sérialisé
		String jsonContent = node.toString();

		mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(jsonContent).with(jwt()))
				.andExpect(status().isCreated())
				// Vérifie que l'en-tête "Location" contient l'ID du nouvel utilisateur
				.andExpect(header().string("Location", containsString("/api/users/")))
				.andExpect(jsonPath("$.email").value("alice.smith@example.com"))
				.andExpect(jsonPath("$.firstName").value("Alice")).andExpect(jsonPath("$.lastName").value("Smith"));

		User createdUser = userRepository.findByEmail("alice.smith@example.com")
				.orElseThrow(() -> new AssertionError("Utilisateur non trouvé"));
		assertTrue(bCryptPasswordEncoder.matches("secret", createdUser.getPassword()),
				"Le mot de passe stocké doit correspondre au mot de passe brut 'secret'");
	}

	/**
	 * Teste que la création d'un utilisateur échoue si le champ "type" n'est pas présent dans le JSON.
	 */
	@Test
	public void testCreateUserMissingTypeFails() throws Exception {
		LanguageDto languageDto = new LanguageDto();
		languageDto.setId(getMainLanguage().getId());

		UserDetailDto newUser = new ExporterDetailDto();
		newUser.setFirstName("Charlie");
		newUser.setLastName("Brown");
		newUser.setEmail("charlie.brown@example.com");
		newUser.setPassword("secret");
		newUser.setLanguage(languageDto);

		ObjectNode node = objectMapper.valueToTree(newUser);
		node.put("password", newUser.getPassword());
		node.remove("type"); // Supprime le champ "type" pour simuler l'absence de ce champ

		String jsonContent = node.toString();

		String responseContent = mockMvc
				.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(jsonContent).with(jwt()))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

		Map<String, Object> responseMap = objectMapper.readValue(responseContent, Map.class);
		String errorMessage = (String) responseMap.get("error");

		assertTrue(errorMessage.contains("Le champ discriminant 'type' est obligatoire pour le type d'utilisateur."),
				"Le message d'erreur doit mentionner le problème lié au champ 'type'");
	}

	/**
	 * Teste la récupération de la liste de tous les utilisateurs.
	 * 
	 */
	@Test
	public void testListUsers() throws Exception {
		mockMvc.perform(get("/api/users").accept(MediaType.APPLICATION_JSON).with(jwt())).andExpect(status().isOk())
				// print
				.andDo(print()).andExpect(jsonPath("$").isArray());
	}

	/**
	 * Teste la mise à jour d'un utilisateur existant.
	 * 
	 */
	@Test
	public void testUpdateUser() throws Exception {

		LanguageDto languageDto = new LanguageDto();
		languageDto.setId(getMainLanguage().getId());

		UserDetailDto updateUser = new AdminDetailDto();
		updateUser.setFirstName("John Updated");
		updateUser.setLastName("Doe Updated");
		updateUser.setEmail("email@updated.com");
		updateUser.setPassword("newpassword");
		updateUser.setRoles(Set.of(new RoleDto(null, getAdminTestRole().getName())));
		updateUser.setLanguage(languageDto);

		int userRoleSize = getMainTestUser().getRoles().size();

		ObjectNode node = objectMapper.valueToTree(updateUser);
		node.put("password", updateUser.getPassword()); // Ajout manuel car le mot de passe n'est pas sérialisé
		String jsonContent = node.toString();

		mockMvc.perform(put("/api/users/" + getMainTestUser().getId()).contentType(MediaType.APPLICATION_JSON)
				.content(jsonContent).with(jwt())).andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName").value("John Updated"));

		// Vérifie que le mot de passe est correctement haché après la mise à jour
		User updatedUser = userRepository.findByEmail("email@updated.com")
				.orElseThrow(() -> new AssertionError("Utilisateur non trouvé"));
		// Vérifie que les rôles ne sont pas mis à jour
		assertEquals(updatedUser.getRoles().size(), userRoleSize, "Les rôles ne doivent pas être mis à jour");
		assertEquals(updatedUser.getRoles().iterator().next().getName(), getUserTestRole().getName(),
				"Le rôle ne doit pas être mis à jour avec le rôle ADMIN");
		assertTrue(bCryptPasswordEncoder.matches("newpassword", updatedUser.getPassword()),
				"Le mot de passe stocké doit correspondre au nouveau mot de passe 'newpassword'");
	}

	/**
	 * Teste la suppression d'un utilisateur.
	 * 
	 */
	@Test
	public void testDeleteUser() throws Exception {
		// TODO delete
		// mockMvc.perform(delete("/api/users/" + getSecondTestUser().getId()).with(jwt()))
		// .andExpect(status().isNoContent());
		//
		// mockMvc.perform(get("/api/users/" +
		// getSecondTestUser().getId()).with(jwt())).andExpect(status().isNotFound());
	}

	/**
	 * Teste l'ajout d'un rôle à un utilisateur.
	 * 
	 */
	@Test
	public void testAddRoleToUserIntegration() throws Exception {
		Integer userId = getMainTestUser().getId();

		mockMvc.perform(post("/api/users/" + userId + "/roles/" + getAdminTestRole().getName())
				.accept(MediaType.APPLICATION_JSON).with(jwt())).andExpect(status().isOk())
				.andExpect(jsonPath("$.roles").isArray())
				.andExpect(jsonPath("$.roles[?(@.name=='" + getAdminTestRole().getName() + "')]").exists());
	}

	/**
	 * Teste la mise à jour complète des rôles d'un utilisateur.
	 * 
	 */
	@Test
	public void testUpdateUserRolesIntegration() throws Exception {
		Integer userId = getMainTestUser().getId();
		List<String> roleNames = List.of(getUserTestRole().getName(), getAdminTestRole().getName());
		String jsonContent = objectMapper.writeValueAsString(roleNames);

		mockMvc.perform(put("/api/users/" + userId + "/roles").contentType(MediaType.APPLICATION_JSON)
				.content(jsonContent).accept(MediaType.APPLICATION_JSON).with(jwt())).andExpect(status().isOk())
				.andExpect(jsonPath("$.roles").isArray())
				.andExpect(jsonPath("$.roles[?(@.name=='ROLE_ADMIN')]").exists())
				.andExpect(jsonPath("$.roles[?(@.name=='ROLE_USER')]").exists());
	}

	/**
	 * Teste la récupération d'un utilisateur inexistant.
	 * 
	 */
	@Test
	public void testGetUserNotFound() throws Exception {
		mockMvc.perform(get("/api/users/999999").with(jwt())).andExpect(status().isNotFound());
	}

	/**
	 * Teste la création d'un utilisateur sans email.
	 * 
	 */
	@Test
	public void testCreateUserMissingEmail() throws Exception {
		UserDetailDto newUser = new AdminDetailDto();
		newUser.setFirstName("Bob");
		newUser.setLastName("Smith");
		newUser.setPassword("secret");

		ObjectNode node = objectMapper.valueToTree(newUser);
		node.put("password", newUser.getPassword());
		String jsonContent = node.toString();

		mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(jsonContent).with(jwt()))
				.andDo(print()).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors.email", containsString("L'adresse email est requise")));
	}

	/**
	 * Teste l'ajout d'un rôle à un utilisateur inexistant.
	 * 
	 */
	@Test
	public void testAddRoleToUserUserNotFound() throws Exception {
		int nonExistentUserId = 999999;
		mockMvc.perform(post("/api/users/" + nonExistentUserId + "/roles/" + getUserTestRole().getName()).with(jwt()))
				.andExpect(status().isNotFound());
	}

	/**
	 * Teste l'ajout d'un rôle inexistant à un utilisateur.
	 * 
	 */
	@Test
	public void testAddRoleToUserRoleNotFound() throws Exception {
		Integer userId = getMainTestUser().getId();
		String nonExistentRoleName = "ROLE_NON_EXISTENT";
		mockMvc.perform(post("/api/users/" + userId + "/roles/" + nonExistentRoleName).with(jwt()))
				.andExpect(status().isNotFound());
	}

	/**
	 * Teste la mise à jour des rôles d'un utilisateur inexistant.
	 * 
	 */
	@Test
	public void testUpdateUserRolesUserNotFound() throws Exception {
		int nonExistentUserId = 999999;
		List<String> roleNames = List.of(getUserTestRole().getName());
		String jsonContent = objectMapper.writeValueAsString(roleNames);

		mockMvc.perform(put("/api/users/" + nonExistentUserId + "/roles").contentType(MediaType.APPLICATION_JSON)
				.content(jsonContent).with(jwt())).andExpect(status().isNotFound());
	}

	/**
	 * Teste la mise à jour des rôles d'un utilisateur lorsqu'un rôle spécifié est inexistant.
	 * 
	 */
	@Test
	public void testUpdateUserRolesRoleNotFound() throws Exception {
		Integer userId = getMainTestUser().getId();
		List<String> roleNames = List.of("ROLE_NON_EXISTENT");
		String jsonContent = objectMapper.writeValueAsString(roleNames);

		mockMvc.perform(put("/api/users/" + userId + "/roles").contentType(MediaType.APPLICATION_JSON)
				.content(jsonContent).with(jwt())).andExpect(status().isNotFound());
	}

	@Test
	public void testProtectedEndpointsWithoutJwt() throws Exception {
		Integer userId = getMainTestUser().getId();

		mockMvc.perform(get("/api/users/" + userId).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());

		mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isUnauthorized());

		mockMvc.perform(put("/api/users/" + userId).contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isUnauthorized());

		mockMvc.perform(delete("/api/users/" + userId)).andExpect(status().isUnauthorized());

		mockMvc.perform(post("/api/users/" + userId + "/roles/ROLE_USER")).andExpect(status().isUnauthorized());

		mockMvc.perform(put("/api/users/" + userId + "/roles").contentType(MediaType.APPLICATION_JSON).content("[]"))
				.andExpect(status().isUnauthorized());
	}
}
