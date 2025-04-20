package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import be.labil.anacarde.domain.dto.RegionDto;
import be.labil.anacarde.domain.model.Region;
import be.labil.anacarde.infrastructure.persistence.RegionRepository;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RegionApiControllerIntegrationTest extends AbstractIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	protected RegionRepository regionRepository;

	private RequestPostProcessor jwt() {
		return request -> {
			request.setCookies(getJwtCookie());
			return request;
		};
	}

	/**
	 * Teste la récupération d'une région.
	 */
	@Test
	public void testGetRegion() throws Exception {
		mockMvc.perform(
				get("/api/regions/" + getMainTestRegion().getId()).accept(MediaType.APPLICATION_JSON).with(jwt()))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(jsonPath("$.name").value(getMainTestRegion().getName()));
	}

	/**
	 * Teste la création d'une région.
	 */
	@Test
	public void testCreateRegion() throws Exception {
		RegionDto newRegionDto = new RegionDto();
		newRegionDto.setName("Nouvelle Région");

		ObjectNode node = objectMapper.valueToTree(newRegionDto);
		String jsonContent = node.toString();

		mockMvc.perform(post("/api/regions").contentType(MediaType.APPLICATION_JSON).content(jsonContent).with(jwt()))
				.andExpect(status().isCreated()).andExpect(header().string("Location", containsString("/api/regions/")))
				.andExpect(jsonPath("$.name").value("Nouvelle Région"));

		Region createdRegion = regionRepository.findAll().stream().filter(r -> "Nouvelle Région".equals(r.getName()))
				.findFirst().orElseThrow(() -> new AssertionError("Région non trouvée"));
	}

	/**
	 * Teste la récupération de la liste des régions.
	 */
	@Test
	public void testListRegions() throws Exception {
		mockMvc.perform(get("/api/regions").accept(MediaType.APPLICATION_JSON).with(jwt())).andExpect(status().isOk())
				.andDo(print()).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(regionRepository.findAll().size()));
	}

	/**
	 * Teste la récupération de la liste des régions.
	 */
	@Test
	public void testListRegionsByCarrier() throws Exception {
		mockMvc.perform(get("/api/regions?carrierId=" + getMainTestCarrier().getId()).accept(MediaType.APPLICATION_JSON)
				.with(jwt())).andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(1));
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

		mockMvc.perform(put("/api/regions/" + getMainTestRegion().getId()).contentType(MediaType.APPLICATION_JSON)
				.content(jsonContent).with(jwt())).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Région Modifiée"));
	}

	/**
	 * Teste la suppression d'une région.
	 */
	@Test
	public void testDeleteRegion() throws Exception {
//		mockMvc.perform(delete("/api/regions/" + getMainTestRegion().getId()).with(jwt()))
//				.andExpect(status().isNoContent());
//
//		mockMvc.perform(get("/api/regions/" + getMainTestRegion().getId()).with(jwt()))
//				.andExpect(status().isNotFound());
	}

	/**
	 * Teste l'ajout d'un transporteur à une région.
	 */
	@Test
	public void testAddCarrier() throws Exception {
		Integer carrierId = getMainTestCarrier().getId();
		Integer regionId = getMainTestRegion().getId();

		mockMvc.perform(put("/api/regions/" + regionId + "/carriers/" + carrierId)
				.contentType(MediaType.APPLICATION_JSON).with(jwt())).andExpect(status().isOk())
				.andExpect(jsonPath("$").doesNotExist());
	}
}