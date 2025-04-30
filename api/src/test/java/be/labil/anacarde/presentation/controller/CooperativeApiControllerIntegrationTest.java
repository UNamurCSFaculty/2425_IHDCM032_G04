package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.CooperativeDto;
import be.labil.anacarde.domain.dto.user.ProducerDetailDto;
import be.labil.anacarde.domain.model.Cooperative;
import be.labil.anacarde.infrastructure.persistence.CooperativeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.LocalDateTime;
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
public class CooperativeApiControllerIntegrationTest extends AbstractIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private CooperativeRepository cooperativeRepository;

	private RequestPostProcessor jwt() {
		return request -> {
			request.setCookies(getJwtCookie());
			return request;
		};
	}

	/**
	 * Teste la récupération d'une coopérative.
	 *
	 */
	@Test
	public void testGetCooperative() throws Exception {
		Cooperative coop = getMainTestCooperative();

		mockMvc.perform(get("/api/cooperatives/" + coop.getId()).accept(MediaType.APPLICATION_JSON).with(jwt()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.name").value(coop.getName()))
				.andExpect(jsonPath("$.address").value(coop.getAddress()))
				.andExpect(jsonPath("$.president.id").value(coop.getPresident().getId()));
	}

	/**
	 * Teste la création d'une coopérative.
	 *
	 */
	@Test
	public void testCreateCooperative() throws Exception {
		CooperativeDto dto = new CooperativeDto();
		dto.setName("Coopérative de Natitingou");
		dto.setAddress("Quartier Kpébié, Natitingou, Bénin");

		ProducerDetailDto producerDetailDto = new ProducerDetailDto();
		producerDetailDto.setId(getSecondTestProducer().getId());
		dto.setPresident(producerDetailDto);

		ObjectNode json = objectMapper.valueToTree(dto);

		mockMvc.perform(
				post("/api/cooperatives").contentType(MediaType.APPLICATION_JSON).content(json.toString()).with(jwt()))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/api/cooperatives/")))
				.andExpect(jsonPath("$.name").value("Coopérative de Natitingou"))
				.andExpect(jsonPath("$.address").value("Quartier Kpébié, Natitingou, Bénin"))
				.andExpect(jsonPath("$.president.id").value(getSecondTestProducer().getId()));

		Cooperative created = cooperativeRepository.findAll().stream()
				.filter(c -> "Coopérative de Natitingou".equals(c.getName())).findFirst()
				.orElseThrow(() -> new AssertionError("Coopérative non trouvée"));
	}

	/**
	 * Teste la récupération de la liste de toutes les coopératives.
	 *
	 */
	@Test
	public void testListCooperatives() throws Exception {
		mockMvc.perform(get("/api/cooperatives").accept(MediaType.APPLICATION_JSON).with(jwt()))
				.andExpect(status().isOk()).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(1)).andDo(print());
	}

	/**
	 * Teste la mise à jour d'une coopérative.
	 *
	 */
	@Test
	public void testUpdateCooperative() throws Exception {
		Cooperative cooperative = getMainTestCooperative();
		CooperativeDto dto = new CooperativeDto();

		ProducerDetailDto producerDetailDto = new ProducerDetailDto();
		producerDetailDto.setId(getMainTestCooperative().getPresident().getId());
		dto.setPresident(producerDetailDto);

		dto.setName("Coop MAJ");
		dto.setAddress(cooperative.getAddress());
		dto.setCreationDate(LocalDateTime.of(2020, 5, 20, 0, 0));

		ObjectNode json = objectMapper.valueToTree(dto);

		mockMvc.perform(put("/api/cooperatives/" + cooperative.getId()).contentType(MediaType.APPLICATION_JSON)
				.content(json.toString()).with(jwt())).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Coop MAJ"))
				.andExpect(jsonPath("$.address").value(cooperative.getAddress()))
				.andExpect(jsonPath("$.creationDate").value("2020-05-20T00:00:00"))
				.andExpect(jsonPath("$.president.id").value(cooperative.getPresident().getId()));

	}

	/**
	 * Teste la suppression d'une coopérative.
	 *
	 */
	@Test
	public void testDeleteCooperative() throws Exception {
		mockMvc.perform(delete("/api/cooperatives/" + getMainTestCooperative().getId()).with(jwt()))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/cooperatives/" + getMainTestCooperative().getId()).with(jwt()))
				.andExpect(status().isNotFound());
	}
}
