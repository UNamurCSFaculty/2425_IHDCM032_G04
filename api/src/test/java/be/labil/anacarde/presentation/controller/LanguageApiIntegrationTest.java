package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.db.LanguageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class LanguageApiIntegrationTest extends AbstractIntegrationTest {

	private @Autowired ObjectMapper objectMapper;

	/**
	 * Teste la création d'une nouvelle langue avec succès.
	 */
	@Test
	void shouldCreateLanguageSuccessfully() throws Exception {
		LanguageDto dto = new LanguageDto("it", "Italien");

		mockMvc.perform(post("/api/languages").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.code", is("it")))
				.andExpect(jsonPath("$.name", is("Italien")));
	}

	/**
	 * Teste la récupération d'une langue existante par son identifiant.
	 */
	@Test
	void shouldReturnLanguageById() throws Exception {

		mockMvc.perform(get("/api/languages/{id}", getMainLanguage().getId()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.code", is("fr")))
				.andExpect(jsonPath("$.name", is("Français")));
	}

	/**
	 * Teste la récupération de la liste complète des langues.
	 */
	@Test
	void shouldListAllLanguages() throws Exception {
		mockMvc.perform(get("/api/languages")).andExpect(status().isOk())
				.andExpect(jsonPath("$", isA(Iterable.class)));
	}

	/**
	 * Teste la mise à jour d'une langue.
	 */
	@Test
	public void testUpdateLanguage() throws Exception {
		LanguageDto updateLanguage = new LanguageDto();
		updateLanguage.setName("Flamand");
		updateLanguage.setCode("nl");

		ObjectNode node = objectMapper.valueToTree(updateLanguage);
		String jsonContent = node.toString();

		mockMvc.perform(put("/api/languages/" + getMainLanguage().getId())
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Flamand"));
	}

	/**
	 * Teste la suppression d'une langue.
	 */
	@Test
	public void testDeleteLanguage() throws Exception {
		// Suppression de la langue
		// mockMvc.perform(delete("/api/languages/" + getMainLanguage().getId()))
		// .andExpect(status().isNoContent());
		//
		// // Vérifie qu'elle est bien supprimée
		// mockMvc.perform(get("/api/languages/" + getMainLanguage().getId()))
		// .andExpect(status().isNotFound());
	}

}
