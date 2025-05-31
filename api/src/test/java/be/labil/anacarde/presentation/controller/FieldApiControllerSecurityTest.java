package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.dto.write.FieldUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Tests de sécurité pour le contrôleur des champs. */
public class FieldApiControllerSecurityTest extends AbstractIntegrationTest {

	private @Autowired ObjectMapper objectMapper;

	@Test
	public void testGetFieldByUserShouldSucceed() throws Exception {
		mockMvc.perform(get("/api/fields/" + getMainTestField().getId()).with(jwtProducer())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void testGetAllFieldsByUserShouldSucceed() throws Exception {
		mockMvc.perform(get("/api/fields").with(jwtProducer()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void testCreateFieldForSameUserShouldSucceed() throws Exception {
		final Integer expectedUser = getProducerTestUser().getId();
		final RequestPostProcessor actualUser = jwtProducer();

		FieldUpdateDto newField = new FieldUpdateDto();
		newField.setAmount(new BigDecimal("999.99"));
		newField.setStatusId(getTestTradeStatus().getId());
		newField.setCreationDate(LocalDateTime.now());
		newField.setTraderId(expectedUser);
		newField.setAuctionId(getTestAuction().getId());

		ObjectNode node = objectMapper.valueToTree(newField);
		String jsonContent = node.toString();

		mockMvc.perform(post("/api/fields").with(actualUser).contentType(MediaType.APPLICATION_JSON)
				.content(jsonContent)).andExpect(status().isCreated());
	}

	@Test
	public void testCreateFieldForAnotherUserShouldFail() throws Exception {
		final Integer expectedUser = getProducerTestUser().getId();
		final RequestPostProcessor actualUser = jwtTransformer();

		FieldUpdateDto newField = new FieldUpdateDto();
		newField.setAmount(new BigDecimal("999.99"));
		newField.setStatusId(getTestTradeStatus().getId());
		newField.setCreationDate(LocalDateTime.now());
		newField.setTraderId(expectedUser);
		newField.setAuctionId(getTestAuction().getId());

		ObjectNode node = objectMapper.valueToTree(newField);
		String jsonContent = node.toString();

		mockMvc.perform(post("/api/fields").with(actualUser).contentType(MediaType.APPLICATION_JSON)
				.content(jsonContent)).andExpect(status().is4xxClientError());
	}

	@Test
	public void testUpdateFieldForSameUserShouldSucceed() throws Exception {
		final Integer expectedUser = getProducerTestUser().getId();
		final RequestPostProcessor actualUser = jwtProducer();

		FieldUpdateDto updateField = new FieldUpdateDto();
		updateField.setAmount(new BigDecimal("1234567.01"));
		updateField.setStatusId(getTestTradeStatus().getId());
		updateField.setCreationDate(LocalDateTime.now());
		updateField.setTraderId(expectedUser);
		updateField.setAuctionId(getTestAuction().getId());

		ObjectNode node = objectMapper.valueToTree(updateField);
		String jsonContent = node.toString();

		mockMvc.perform(put("/api/fields/" + getTestField().getId()).with(actualUser)
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isOk());
	}

	@Test
	public void testUpdateFieldForAnotherUserShouldFail() throws Exception {
		final Integer expectedUser = getProducerTestUser().getId();
		final RequestPostProcessor actualUser = jwtTransformer();

		FieldUpdateDto updateField = new FieldUpdateDto();
		updateField.setAmount(new BigDecimal("1234567.01"));
		updateField.setStatusId(getTestTradeStatus().getId());
		updateField.setCreationDate(LocalDateTime.now());
		updateField.setTraderId(expectedUser);
		updateField.setAuctionId(getTestAuction().getId());

		ObjectNode node = objectMapper.valueToTree(updateField);
		String jsonContent = node.toString();

		mockMvc.perform(put("/api/fields/" + getTestField().getId()).with(actualUser)
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void testDeleteFieldForSameUserShouldSucceed() throws Exception {
		// expectedUser = producer
		final RequestPostProcessor actualUser = jwtProducer();

		mockMvc.perform(delete("/api/fields/" + getTestField().getId()).with(actualUser))
				.andExpect(status().isNoContent());
	}

	@Test
	public void testDeleteFieldForAnotherUserShouldFail() throws Exception {
		// expectedUser = producer
		final RequestPostProcessor actualUser = jwtCarrier();

		mockMvc.perform(delete("/api/fields/" + getTestField().getId()).with(actualUser))
				.andExpect(status().is4xxClientError());
	}
}
