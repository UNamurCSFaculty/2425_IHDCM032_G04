package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import be.labil.anacarde.domain.dto.db.AddressDto;
import be.labil.anacarde.domain.dto.write.user.AdminUpdateDto;
import be.labil.anacarde.domain.dto.write.user.ExporterUpdateDto;
import be.labil.anacarde.domain.dto.write.user.ProducerUpdateDto;
import be.labil.anacarde.domain.dto.write.user.UserUpdateDto;
import be.labil.anacarde.domain.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** Tests d'intégration pour le contrôleur des utilisateurs. */
public class UserControllerApiControllerIntegrationTest extends AbstractIntegrationTest {

	private @Autowired ObjectMapper objectMapper;
	private @Autowired BCryptPasswordEncoder bCryptPasswordEncoder;

	/**
	 * Teste la récupération d'un utilisa teur existant.
	 * 
	 */
	@Test
	public void testGetUser() throws Exception {
		mockMvc.perform(
				get("/api/users/" + getMainTestUser().getId()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value(getMainTestUser().getEmail()));
	}

	/**
	 * Teste la création d'un nouvel utilisateur.
	 */
	@Test
	public void testCreateUser() throws Exception {
		ProducerUpdateDto newUser = new ProducerUpdateDto();
		newUser.setFirstName("Alice");
		newUser.setLastName("Smith");
		newUser.setEmail("alice.smith@example.com");
		newUser.setAddress(
				AddressDto.builder().street("Rue de la paix").cityId(1).regionId(1).build());
		newUser.setPassword("secret!!!");
		newUser.setLanguageId(getMainLanguageDto().getId());
		newUser.setPhone("+2290197005502");
		newUser.setAgriculturalIdentifier("TS450124");
		newUser.setCooperativeId(getMainTestCooperative().getId());

		byte[] json = objectMapper.writeValueAsBytes(newUser);
		MockMultipartFile userPart = new MockMultipartFile("user", "user.json",
				MediaType.APPLICATION_JSON_VALUE, json);

		mockMvc.perform(multipart("/api/users").file(userPart).with(jwtAndCsrf())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
				.andExpect(jsonPath("$.address").exists())
				.andExpect(jsonPath("$.address.street").value("Rue de la paix"))
				.andExpect(jsonPath("$.address.cityId").value(1))
				.andExpect(jsonPath("$.address.regionId").value(1))
				.andExpect(jsonPath("$.email").value("alice.smith@example.com"))
				.andExpect(jsonPath("$.firstName").value("Alice"))
				.andExpect(jsonPath("$.lastName").value("Smith"));

		User createdUser = userRepository.findByEmail("alice.smith@example.com")
				.orElseThrow(() -> new AssertionError("Utilisateur non trouvé"));
		assertTrue(bCryptPasswordEncoder.matches("secret!!!", createdUser.getPassword()),
				"Le mot de passe stocké doit correspondre au mot de passe brut 'secret!!!'");
	}

	/**
	 * Teste que la création d'un utilisateur échoue si le champ "type" n'est pas présent.
	 */
	@Test
	public void testCreateUserMissingTypeFails() throws Exception {
		UserUpdateDto newUser = new ExporterUpdateDto();
		newUser.setFirstName("Charlie");
		newUser.setLastName("Brown");
		newUser.setEmail("charlie.brown@example.com");
		newUser.setAddress(
				AddressDto.builder().street("Rue de la paix").cityId(1).regionId(1).build());
		newUser.setPassword("secret");
		newUser.setLanguageId(getMainLanguageDto().getId());

		ObjectNode node = objectMapper.valueToTree(newUser);
		node.put("password", newUser.getPassword());
		node.remove("type");
		byte[] json = objectMapper.writeValueAsBytes(node);
		MockMultipartFile userPart = new MockMultipartFile("user", "user.json",
				MediaType.APPLICATION_JSON_VALUE, json);

		mockMvc.perform(multipart("/api/users").file(userPart).with(jwtAndCsrf()))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors[0].message",
						containsString("Le champ discriminant 'type' est obligatoire.")));
	}

	/**
	 * Teste la récupération de la liste de tous les utilisateurs.
	 * 
	 */
	@Test
	public void testListUsers() throws Exception {
		mockMvc.perform(get("/api/users").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				// print
				.andExpect(jsonPath("$").isArray());
	}

	/**
	 * Teste la mise à jour d'un utilisateur existant.
	 * 
	 */
	@Test
	public void testUpdateUser() throws Exception {
		UserUpdateDto updateUser = new AdminUpdateDto();
		updateUser.setFirstName("John Updated");
		updateUser.setLastName("Doe Updated");
		updateUser.setEmail("email@updated.com");
		updateUser.setAddress(
				AddressDto.builder().street("Rue de la paix").cityId(1).regionId(1).build());
		updateUser.setPassword("newpassword");
		updateUser.setLanguageId(getMainLanguageDto().getId());

		ObjectNode node = objectMapper.valueToTree(updateUser);
		node.put("password", updateUser.getPassword());

		int userRoleSize = getMainTestUser().getRoles().size();

		String jsonContent = node.toString();

		mockMvc.perform(put("/api/users/" + getMainTestUser().getId())
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName").value("John Updated"));

		// Vérifie que le mot de passe est correctement haché après la mise à jour
		User updatedUser = userRepository.findByEmail("email@updated.com")
				.orElseThrow(() -> new AssertionError("Utilisateur non trouvé"));
		// Vérifie que les rôles ne sont pas mis à jour
		assertEquals(updatedUser.getRoles().size(), userRoleSize,
				"Les rôles ne doivent pas être mis à jour");
		assertEquals(updatedUser.getRoles().iterator().next().getName(),
				getUserTestRole().getName(),
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
		// mockMvc.perform(delete("/api/users/" + getSecondTestUser().getId()))
		// .andExpect(status().isNoContent());
		//
		// mockMvc.perform(get("/api/users/" +
		// getSecondTestUser().getId())).andExpect(status().isNotFound());
	}

	/**
	 * Teste l'ajout d'un rôle à un utilisateur.
	 * 
	 */
	@Test
	public void testAddRoleToUserIntegration() throws Exception {
		Integer userId = getMainTestUser().getId();

		mockMvc.perform(post("/api/users/" + userId + "/roles/" + getAdminTestRole().getName())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.roles").isArray())
				.andExpect(jsonPath("$.roles[?(@.name=='" + getAdminTestRole().getName() + "')]")
						.exists());
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

		mockMvc.perform(
				put("/api/users/" + userId + "/roles").contentType(MediaType.APPLICATION_JSON)
						.content(jsonContent).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.roles").isArray())
				.andExpect(jsonPath("$.roles[?(@.name=='ROLE_ADMIN')]").exists())
				.andExpect(jsonPath("$.roles[?(@.name=='ROLE_USER')]").exists());
	}

	/**
	 * Teste la récupération d'un utilisateur inexistant.
	 * 
	 */
	@Test
	public void testGetUserNotFound() throws Exception {
		mockMvc.perform(get("/api/users/999999")).andExpect(status().isNotFound());
	}

	/**
	 * Teste la création d'un utilisateur sans email.
	 */
	@Test
	public void testCreateUserMissingEmail() throws Exception {
		UserUpdateDto newUser = new AdminUpdateDto();
		newUser.setFirstName("Bob");
		newUser.setLastName("Smith");
		newUser.setAddress(
				AddressDto.builder().street("Rue de la paix").cityId(1).regionId(1).build());
		newUser.setPassword("secret");

		ObjectNode node = objectMapper.valueToTree(newUser);
		node.put("password", newUser.getPassword());
		byte[] json = objectMapper.writeValueAsBytes(node);
		MockMultipartFile userPart = new MockMultipartFile("user", "user.json",
				MediaType.APPLICATION_JSON_VALUE, json);

		mockMvc.perform(multipart("/api/users").file(userPart).with(jwtAndCsrf()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code", containsString("validation.error")));
	}

	/**
	 * Teste l'ajout d'un rôle à un utilisateur inexistant.
	 * 
	 */
	@Test
	public void testAddRoleToUserUserNotFound() throws Exception {
		int nonExistentUserId = 999999;
		mockMvc.perform(
				post("/api/users/" + nonExistentUserId + "/roles/" + getUserTestRole().getName()))
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
		mockMvc.perform(post("/api/users/" + userId + "/roles/" + nonExistentRoleName))
				.andExpect(status().isNotFound());
	}

	/**
	 * Teste la mise à jour des rôles d'un utilisateur inexistant.
	 */
	@Test
	public void testUpdateUserRolesUserNotFound() throws Exception {
		int nonExistentUserId = 999999;
		List<String> roleNames = List.of(getUserTestRole().getName());
		String jsonContent = objectMapper.writeValueAsString(roleNames);

		mockMvc.perform(put("/api/users/" + nonExistentUserId + "/roles")
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isNotFound());
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

		mockMvc.perform(put("/api/users/" + userId + "/roles")
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isNotFound());
	}
}
