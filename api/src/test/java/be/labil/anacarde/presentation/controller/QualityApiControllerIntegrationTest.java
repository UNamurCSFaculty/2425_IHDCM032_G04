package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.QualityDto;
import be.labil.anacarde.domain.model.Quality;
import be.labil.anacarde.infrastructure.persistence.QualityRepository;
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

/** Tests d'intégration pour le contrôleur des qualités. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class QualityApiControllerIntegrationTest extends AbstractIntegrationTest {

	private @Autowired MockMvc mockMvc;
	private @Autowired ObjectMapper objectMapper;
	@Autowired
	protected QualityRepository qualityRepository;

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
	 * Teste la récupération d'une qualité existant.
	 * 
	 */
	@Test
	public void testGetQuality() throws Exception {
		mockMvc.perform(
				get("/api/qualities/" + getMainTestQuality().getId()).accept(MediaType.APPLICATION_JSON).with(jwt()))
				.andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("$.name").value("WW160"));
	}

	/**
	 * Teste la création d'une nouvel qualité.
	 * 
	 */
	@Test
	public void testCreateQuality() throws Exception {
		QualityDto newQuality = new QualityDto();
		newQuality.setName("ZZZ-000");

		ObjectNode node = objectMapper.valueToTree(newQuality);
		String jsonContent = node.toString();

		mockMvc.perform(post("/api/qualities").contentType(MediaType.APPLICATION_JSON).content(jsonContent).with(jwt()))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/api/qualities/")))
				.andExpect(jsonPath("$.name").value("ZZZ-000"));

		Quality createdQuality = qualityRepository.findAll().stream()
				.filter(quality -> quality.getName().equals("ZZZ-000")).findFirst()
				.orElseThrow(() -> new AssertionError("Qualitée non trouvée"));
	}

	/**
	 * Teste la récupération de la liste de tous les qualités.
	 * 
	 */
	@Test
	public void testListQualities() throws Exception {
		mockMvc.perform(get("/api/qualities").accept(MediaType.APPLICATION_JSON).with(jwt())).andExpect(status().isOk())
				.andDo(print()).andExpect(jsonPath("$").isArray());
	}

	/**
	 * Teste la mise à jour d'une qualité.
	 * 
	 */
	@Test
	public void testUpdateQuality() throws Exception {
		QualityDto updateQuality = new QualityDto();
		updateQuality.setName("WW450");

		ObjectNode node = objectMapper.valueToTree(updateQuality);
		String jsonContent = node.toString();

		mockMvc.perform(put("/api/qualities/" + getMainTestQuality().getId()).contentType(MediaType.APPLICATION_JSON)
				.content(jsonContent).with(jwt())).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("WW450"));
	}

	/**
	 * Teste la suppression d'une qualité.
	 * 
	 */
	@Test
	public void testDeleteQuality() throws Exception {
		// TODO delete
		// mockMvc.perform(delete("/api/qualities/" + getMainTestQuality().getId()).with(jwt()))
		// .andExpect(status().isNoContent());
		//
		// mockMvc.perform(get("/api/qualities/" +
		// getMainTestQuality().getId()).with(jwt())).andExpect(status().isNotFound());
	}
}
