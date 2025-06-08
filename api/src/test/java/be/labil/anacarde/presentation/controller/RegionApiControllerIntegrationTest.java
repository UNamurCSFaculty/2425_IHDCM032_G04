package be.labil.anacarde.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.db.RegionDto;
import be.labil.anacarde.infrastructure.persistence.RegionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

/*
 * Tests d'intégration pour le contrôleur des régions.
 */
public class RegionApiControllerIntegrationTest extends AbstractIntegrationTest {

	private @Autowired ObjectMapper objectMapper;
	private @Autowired RegionRepository regionRepository;

	/**
	 * Teste la récupération d'une région.
	 */
	@Test
	public void testGetRegion() throws Exception {
		mockMvc.perform(get("/api/regions/" + getMainTestRegion().getId())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value(getMainTestRegion().getName()));
	}

	/**
	 * Teste la récupération de la liste des régions.
	 */
	@Test
	public void testListRegions() throws Exception {
		mockMvc.perform(get("/api/regions").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(regionRepository.findAll().size()));
	}

	/**
	 * Teste la récupération de la liste des régions.
	 */
	@Test
	public void testListRegionsByCarrier() throws Exception {
		mockMvc.perform(get("/api/regions?carrierId=" + getMainTestCarrier().getId())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(1));
	}

	/**
	 * Teste la mise à jour d'une région.
	 */
	@Test
	public void testUpdateRegion() throws Exception {
		RegionDto updateRegion = new RegionDto();
		updateRegion.setName("Région Modifiée");

		ObjectNode node = objectMapper.valueToTree(updateRegion);
		String jsonContent = node.toString();

		mockMvc.perform(put("/api/regions/" + getMainTestRegion().getId())
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Région Modifiée"));
	}
}