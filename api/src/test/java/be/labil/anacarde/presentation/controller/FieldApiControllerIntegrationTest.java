package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.FieldDetailDto;
import be.labil.anacarde.domain.dto.user.ProducerDetailDto;
import be.labil.anacarde.domain.model.Field;
import be.labil.anacarde.infrastructure.persistence.FieldRepository;
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

/** Tests d'intégration pour le contrôleur des champs. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FieldApiControllerIntegrationTest extends AbstractIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	protected FieldRepository fieldRepository;

	private RequestPostProcessor jwt() {
		return request -> {
			request.setCookies(getJwtCookie());
			return request;
		};
	}

	/**
	 * Teste la récupération d'un champ.
	 *
	 */
	@Test
	public void testGetField() throws Exception {
		mockMvc.perform(get("/api/users/" + getProducerTestUser().getId() + "/fields/" + getMainTestField().getId())
				.accept(MediaType.APPLICATION_JSON).with(jwt())).andExpect(status().isOk()).andDo(print())
				.andExpect(jsonPath("$.location").value("POINT (2.3522 48.8566)"))
				.andExpect(jsonPath("$.identifier").value(getMainTestField().getIdentifier()));
	}

	/**
	 * Teste la création d'un champ.
	 *
	 */
	@Test
	public void testCreateField() throws Exception {
		FieldDetailDto newFieldDto = new FieldDetailDto();
		newFieldDto.setLocation("POINT(2.3522 48.8566)");
		newFieldDto.setIdentifier("FIELD-001");
		ProducerDetailDto producerDetailDto = new ProducerDetailDto();
		producerDetailDto.setId(getProducerTestUser().getId());
		newFieldDto.setProducer(producerDetailDto);

		ObjectNode node = objectMapper.valueToTree(newFieldDto);
		String jsonContent = node.toString();

		mockMvc.perform(post("/api/users/" + getProducerTestUser().getId() + "/fields")
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent).with(jwt()))
				.andExpect(status().isCreated()).andExpect(header().string("Location", containsString("/api/users/")))
				.andExpect(jsonPath("$.location").value("POINT (2.3522 48.8566)"))
				.andExpect(jsonPath("$.identifier").value("FIELD-001"))
				.andExpect(jsonPath("$.producer.id").value(getProducerTestUser().getId()));

		Field createdField = fieldRepository.findAll().stream()
				.filter(field -> "FIELD-001".equals(field.getIdentifier())).findFirst()
				.orElseThrow(() -> new AssertionError("Field non trouvé"));
	}

	/**
	 * Teste la récupération de la liste de tous les champs d'un utilisateur donné.
	 *
	 */
	@Test
	public void testListFields() throws Exception {
		mockMvc.perform(get("/api/users/" + getProducerTestUser().getId() + "/fields")
				.accept(MediaType.APPLICATION_JSON).with(jwt())).andExpect(status().isOk()).andDo(print())
				.andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(2));
	}

	/**
	 * Teste la mise à jour d'un champ.
	 *
	 */
	@Test
	public void testUpdateField() throws Exception {
		FieldDetailDto updateField = new FieldDetailDto();
		updateField.setLocation("POINT (1.111 2.222)");
		updateField.setIdentifier("FIELD-UPDATED");
		ProducerDetailDto producerDetailDto = new ProducerDetailDto();
		producerDetailDto.setId(getProducerTestUser().getId());
		updateField.setProducer(producerDetailDto);

		ObjectNode node = objectMapper.valueToTree(updateField);
		String jsonContent = node.toString();

		mockMvc.perform(put("/api/users/" + getProducerTestUser().getId() + "/fields/" + getMainTestField().getId())
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent).with(jwt())).andExpect(status().isOk())
				.andExpect(jsonPath("$.location").value("POINT (1.111 2.222)"))
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
		// getMainTestField().getId()).with(jwt()))
		// .andExpect(status().isNoContent());
		//
		// mockMvc.perform(get("/api/fields/" + getMainTestField().getId()).with(jwt()))
		// .andExpect(status().isNotFound());
	}
}
