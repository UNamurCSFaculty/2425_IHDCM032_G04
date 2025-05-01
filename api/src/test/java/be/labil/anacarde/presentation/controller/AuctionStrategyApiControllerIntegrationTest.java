package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.AuctionStrategyDto;
import be.labil.anacarde.domain.model.AuctionStrategy;
import be.labil.anacarde.infrastructure.persistence.AuctionStrategyRepository;
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

/** Tests d'intégration pour le contrôleur des stratégies d'enchères. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuctionStrategyApiControllerIntegrationTest extends AbstractIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private AuctionStrategyRepository repository;

	private RequestPostProcessor jwt() {
		return request -> {
			request.setCookies(getJwtCookie());
			return request;
		};
	}

	/**
	 * Teste la récupération d'une stratégie d'enchère existante.
	 */
	@Test
	public void testGetAuctionStrategy() throws Exception {
		mockMvc.perform(get("/api/auctions/strategies/" + getTestAuctionStrategy().getId())
				.accept(MediaType.APPLICATION_JSON).with(jwt())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(getTestAuctionStrategy().getId()))
				.andExpect(jsonPath("$.name").value(getTestAuctionStrategy().getName()));
	}

	/**
	 * Teste la création d'une nouvelle stratégie d'enchère.
	 */
	@Test
	public void testCreateAuctionStrategy() throws Exception {
		AuctionStrategyDto dto = new AuctionStrategyDto();
		dto.setName("Best Price Strategy");

		ObjectNode node = objectMapper.valueToTree(dto);
		String jsonContent = node.toString();

		mockMvc.perform(post("/api/auctions/strategies").contentType(MediaType.APPLICATION_JSON).content(jsonContent)
				.with(jwt())).andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/api/auctions/strategies")))
				.andExpect(jsonPath("$.name").value("Best Price Strategy"));

		AuctionStrategy created = repository.findAll().stream().filter(a -> "Best Price Strategy".equals(a.getName()))
				.findFirst().orElseThrow(() -> new AssertionError("AuctionStrategy non trouvée"));
	}

	/**
	 * Teste la récupération de la liste de toutes les stratégies d'enchère.
	 */
	@Test
	public void testListAuctionStrategies() throws Exception {
		mockMvc.perform(get("/api/auctions/strategies").accept(MediaType.APPLICATION_JSON).with(jwt()))
				.andExpect(status().isOk()).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(1));
	}

	/**
	 * Teste la mise à jour d'une stratégie d'enchère existante.
	 */
	@Test
	public void testUpdateAuctionStrategy() throws Exception {
		AuctionStrategyDto dto = new AuctionStrategyDto();
		dto.setName("Updated Strategy");

		ObjectNode node = objectMapper.valueToTree(dto);
		String jsonContent = node.toString();

		mockMvc.perform(put("/api/auctions/strategies/" + getTestAuctionStrategy().getId())
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent).with(jwt())).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Updated Strategy"));
	}

	/**
	 * Teste la suppression d'une stratégie d'enchère.
	 *
	 * // TODO: Décommenter le test lorsque la suppression sera complètement implémentée
	 */
	@Test
	public void testDeleteAuctionStrategy() throws Exception {

		// mockMvc.perform(delete("/api/auctions/strategies/" + getTestAuctionStrategy().getId()).with(jwt()))
		// .andExpect(status().isNoContent());
		//
		// mockMvc.perform(get("/api/auctions/strategies/" + getTestAuctionStrategy().getId()).with(jwt()))
		// .andExpect(status().isNotFound());
	}
}
