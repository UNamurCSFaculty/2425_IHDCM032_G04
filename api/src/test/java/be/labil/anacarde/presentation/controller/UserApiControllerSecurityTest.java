package be.labil.anacarde.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import be.labil.anacarde.domain.dto.write.user.update.AddressUpdateDto;
import be.labil.anacarde.domain.dto.write.user.update.ProducerUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Tests de sécurité pour le contrôleur des utilisateurs. */
public class UserApiControllerSecurityTest extends AbstractIntegrationTest {

	private @Autowired ObjectMapper objectMapper;

	@Test
	public void testGetAllUsersByUserShouldSucceed() throws Exception {
		mockMvc.perform(get("/api/users").with(jwtProducer()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void testUpdateUserForSameUserShouldSucceed() throws Exception {
		final Integer expectedUser = getProducerTestUser().getId();
		final RequestPostProcessor actualUser = jwtProducer();

		ProducerUpdateDto updateUser = new ProducerUpdateDto();
		updateUser.setFirstName("John Updated");
		updateUser.setLastName("Doe Updated");
		updateUser.setEmail("email@updated.com");
		updateUser.setAddress(
				AddressUpdateDto.builder().street("Rue de la paix").cityId(1).regionId(1).build());
		updateUser.setPassword("newpassword");
		updateUser.setLanguageId(getMainLanguageDto().getId());
		updateUser.setAgriculturalIdentifier("AGR-999");
		updateUser.setCooperativeId(getMainTestCooperative().getId());

		ObjectNode node = objectMapper.valueToTree(updateUser);
		node.put("password", updateUser.getPassword());

		String jsonContent = node.toString();

		mockMvc.perform(put("/api/users/" + expectedUser).with(actualUser)
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isOk());
	}

	@Test
	public void testUpdateUserForAnotherUserShouldFail() throws Exception {
		final Integer expectedUser = getProducerTestUser().getId();
		final RequestPostProcessor actualUser = jwtTransformer();

		ProducerUpdateDto updateUser = new ProducerUpdateDto();
		updateUser.setFirstName("John Updated");
		updateUser.setLastName("Doe Updated");
		updateUser.setEmail("email@updated.com");
		updateUser.setAddress(
				AddressUpdateDto.builder().street("Rue de la paix").cityId(1).regionId(1).build());
		updateUser.setPassword("newpassword");
		updateUser.setLanguageId(getMainLanguageDto().getId());
		updateUser.setAgriculturalIdentifier("AGR-999");
		updateUser.setCooperativeId(getMainTestCooperative().getId());

		ObjectNode node = objectMapper.valueToTree(updateUser);
		node.put("password", updateUser.getPassword());

		String jsonContent = node.toString();

		mockMvc.perform(put("/api/users/" + expectedUser).with(actualUser)
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isForbidden());
	}

	@Test
	public void testDeleteUserByNonAdminShouldFail() throws Exception {
		final RequestPostProcessor actualUser = jwtProducer();

		mockMvc.perform(delete("/api/users/" + getProducerTestUser().getId()).with(actualUser))
				.andExpect(status().isForbidden());
	}
}
