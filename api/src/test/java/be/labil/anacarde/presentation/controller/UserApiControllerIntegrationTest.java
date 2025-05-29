package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import be.labil.anacarde.domain.dto.db.AddressDto;
import be.labil.anacarde.domain.dto.write.user.create.AdminCreateDto;
import be.labil.anacarde.domain.dto.write.user.create.ExporterCreateDto;
import be.labil.anacarde.domain.dto.write.user.create.ProducerCreateDto;
import be.labil.anacarde.domain.dto.write.user.create.UserCreateDto;
import be.labil.anacarde.domain.dto.write.user.update.AddressUpdateDto;
import be.labil.anacarde.domain.dto.write.user.update.AdminUpdateDto;
import be.labil.anacarde.domain.dto.write.user.update.UserUpdateDto;
import be.labil.anacarde.domain.model.User;
import be.labil.anacarde.presentation.controller.enums.UserType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** Tests d'intégration pour le contrôleur des utilisateurs. */
public class UserApiControllerIntegrationTest extends AbstractIntegrationTest {

	private @Autowired ObjectMapper objectMapper;
	private @Autowired BCryptPasswordEncoder bCryptPasswordEncoder;

	@AfterEach
	public void cleanUploadDir() {
		Path uploadDir = Paths.get("build/test-uploads");
		if (Files.exists(uploadDir)) {
			try (Stream<Path> paths = Files.walk(uploadDir)) {
				paths.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			} catch (IOException e) {
				System.err.println("Impossible de nettoyer build/test-uploads : " + e.getMessage());
			}
		}
	}

	/**
	 * Teste la création d'un nouvel utilisateur (avec au moins un document téléversé).
	 */
	@Test
	public void testCreateUser() throws Exception {
		// ---------- partie JSON "user" ----------
		ProducerCreateDto newUser = new ProducerCreateDto();
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

		byte[] userJson = objectMapper.writeValueAsBytes(newUser);
		MockMultipartFile userPart = new MockMultipartFile("user", // nom de la part
				"user.json", MediaType.APPLICATION_JSON_VALUE, userJson);

		// ---------- partie binaire "documents" ----------
		byte[] pdf = "dummy pdf content".getBytes();
		MockMultipartFile documentPart = new MockMultipartFile("documents", // même nom que dans
																			// l’API (liste
																			// MultipartFile)
				"doc1.pdf", "application/pdf", pdf);

		// ---------- appel ----------
		mockMvc.perform(multipart("/api/users").file(userPart).file(documentPart) // au moins un
																					// document
				.characterEncoding("UTF-8").with(jwtAndCsrf()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.address").exists())
				.andExpect(jsonPath("$.address.street").value("Rue de la paix"))
				.andExpect(jsonPath("$.address.cityId").value(1))
				.andExpect(jsonPath("$.address.regionId").value(1))
				.andExpect(jsonPath("$.email").value("alice.smith@example.com"))
				.andExpect(jsonPath("$.firstName").value("Alice"))
				.andExpect(jsonPath("$.lastName").value("Smith"))
				.andExpect(jsonPath("$.documents").isArray())
				.andExpect(jsonPath("$.documents.length()").value(1))
				.andExpect(jsonPath("$.documents[0].originalFilename").value("doc1.pdf"))
				.andExpect(jsonPath("$.documents[0].contentType").value("application/pdf"));

		// ---------- vérifications en base ----------
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
		UserCreateDto newUser = new ExporterCreateDto();
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
				AddressUpdateDto.builder().street("Rue de la paix").cityId(1).regionId(1).build());
		updateUser.setPassword("newpassword");
		updateUser.setLanguageId(getMainLanguageDto().getId());

		ObjectNode node = objectMapper.valueToTree(updateUser);
		node.put("password", updateUser.getPassword());

		String jsonContent = node.toString();

		mockMvc.perform(put("/api/users/" + getMainTestUser().getId())
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName").value("John Updated"));

		// Vérifie que le mot de passe est correctement haché après la mise à jour
		User updatedUser = userRepository.findByEmail("email@updated.com")
				.orElseThrow(() -> new AssertionError("Utilisateur non trouvé"));
		assertTrue(bCryptPasswordEncoder.matches("newpassword", updatedUser.getPassword()),
				"Le mot de passe stocké doit correspondre au nouveau mot de passe 'newpassword'");
	}

	/**
	 * Teste la récupération de la liste de tous les utilisateurs.
	 *
	 */
	@Test
	public void testListUsers() throws Exception {
		mockMvc.perform(get("/api/users").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
	}

	/**
	 * Teste la récupération de la liste de tous les utilisateurs par type.
	 *
	 */
	@Test
	public void testListUsersByType() throws Exception {
		mockMvc.perform(
				get("/api/users?userType=" + UserType.producer).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(2));
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
	 * Teste la création d'un utilisateur sans email.
	 */
	@Test
	public void testCreateUserMissingEmail() throws Exception {
		UserCreateDto newUser = new AdminCreateDto();
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
}
