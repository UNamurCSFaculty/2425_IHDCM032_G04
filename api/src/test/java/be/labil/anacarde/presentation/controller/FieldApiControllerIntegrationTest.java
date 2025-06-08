package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.write.AddressUpdateDto;
import be.labil.anacarde.domain.dto.write.FieldUpdateDto;
import be.labil.anacarde.domain.model.Field;
import be.labil.anacarde.infrastructure.persistence.FieldRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

/** Tests d'intégration pour le contrôleur des champs. */
public class FieldApiControllerIntegrationTest extends AbstractIntegrationTest {

	private @Autowired ObjectMapper objectMapper;
	private @Autowired FieldRepository fieldRepository;

	/**
	 * Teste la récupération d'un champ.
	 *
	 */
	@Test
	public void testGetField() throws Exception {
		mockMvc.perform(
				get("/api/fields/" + getMainTestField().getId()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.address.location").value("POINT (3.3522 8.8566)"))
				.andExpect(jsonPath("$.identifier").value(getMainTestField().getIdentifier()));
	}

	/**
	 * Teste la création d'un champ.
	 *
	 */
	@Test
	public void testCreateField() throws Exception {
		FieldUpdateDto newFieldDto = new FieldUpdateDto();

		AddressUpdateDto addressDto = new AddressUpdateDto();
		addressDto.setLocation("POINT (2.3522 48.8566)");
		addressDto.setCityId(1);
		addressDto.setRegionId(1);

		newFieldDto.setAddress(addressDto);
		newFieldDto.setIdentifier("FIELD-666");

		newFieldDto.setProducerId(getProducerTestUser().getId());

		ObjectNode node = objectMapper.valueToTree(newFieldDto);
		String jsonContent = node.toString();

		mockMvc.perform(
				post("/api/fields").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/api/fields")))
				.andExpect(jsonPath("$.address.location").value("POINT (2.3522 48.8566)"))
				.andExpect(jsonPath("$.identifier").value("FIELD-666"));

		Field createdField = fieldRepository.findAll().stream()
				.filter(field -> "FIELD-666".equals(field.getIdentifier())).findFirst()
				.orElseThrow(() -> new AssertionError("Field non trouvé"));
	}

	/**
	 * Teste la récupération de la liste de tous les champs.
	 *
	 */
	@Test
	public void testListFields() throws Exception {
		mockMvc.perform(get("/api/fields").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(2));
	}

	/**
	 * Teste la récupération de la liste de tous les champs d'un utilisateur donné.
	 *
	 */
	@Test
	public void testListFieldsByProducer() throws Exception {
		mockMvc.perform(get("/api/fields?producerId=" + getProducerTestUser().getId())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(2));
	}

	/**
	 * Teste la mise à jour d'un champ.
	 */
	@Test
	public void testUpdateField() throws Exception {
		FieldUpdateDto updateField = new FieldUpdateDto();

		AddressUpdateDto addressDto = new AddressUpdateDto();
		addressDto.setLocation("POINT (1.111 2.222)");
		addressDto.setCityId(1); // à adapter selon les valeurs valides
		addressDto.setRegionId(1); // idem

		updateField.setAddress(addressDto);
		updateField.setIdentifier("FIELD-UPDATED");
		updateField.setProducerId(getProducerTestUser().getId());

		ObjectNode node = objectMapper.valueToTree(updateField);
		String jsonContent = node.toString();

		mockMvc.perform(put("/api/fields/" + getMainTestField().getId())
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.address.location").value("POINT (1.111 2.222)"))
				.andExpect(jsonPath("$.identifier").value("FIELD-UPDATED"));
	}

	/**
	 * Teste la suppression d'un champ.
	 *
	 */
	@Test
	public void testDeleteField() throws Exception {
		// //TODO: Décommenter quand le delete sera activé
		// mockMvc.perform(delete("/api/users/"+ getProducerTestUser().getId() +"/fields/" +
		// getMainTestField().getId()))
		// .andExpect(status().isNoContent());
		//
		// mockMvc.perform(get("/api/fields/" + getMainTestField().getId()))
		// .andExpect(status().isNotFound());
	}

}
