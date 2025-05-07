package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.AuctionDto;
import be.labil.anacarde.domain.dto.AuctionStrategyDto;
import be.labil.anacarde.domain.dto.HarvestProductDto;
import be.labil.anacarde.domain.dto.ProductDto;
import be.labil.anacarde.domain.model.Auction;
import be.labil.anacarde.infrastructure.persistence.AuctionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

/** Tests d'intégration pour le contrôleur des enchères. */
public class AuctionApiControllerIntegrationTest extends AbstractIntegrationTest {

	private @Autowired ObjectMapper objectMapper;
	private @Autowired AuctionRepository auctionRepository;

	/**
	 * Teste la récupération d'un enchère existant.
	 * 
	 */
	@Test
	public void testGetAuction() throws Exception {
		mockMvc.perform(
				get("/api/auctions/" + getTestAuction().getId()).accept(MediaType.APPLICATION_JSON).with(jwtAndCsrf()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.price").value("500.0"))
				.andExpect(jsonPath("$.productQuantity").value("10")).andExpect(jsonPath("$.active").value("true"))
				.andExpect(jsonPath("$.product.weightKg").value("2000.0"));
	}

	/**
	 * Teste la création d'un nouvel enchère.
	 * 
	 */
	@Test
	public void testCreateAuction() throws Exception {
		AuctionStrategyDto strategyDto = new AuctionStrategyDto();
		strategyDto.setId(getTestAuctionStrategy().getId());

		ProductDto productDto = new HarvestProductDto();
		productDto.setId(getTestHarvestProduct().getId());

		AuctionDto newAuction = new AuctionDto();
		newAuction.setPrice(new BigDecimal("111.11"));
		newAuction.setProductQuantity(11);
		newAuction.setActive(true);
		newAuction.setCreationDate(LocalDateTime.now());
		newAuction.setExpirationDate(LocalDateTime.now());
		newAuction.setStrategy(strategyDto);
		newAuction.setProduct(productDto);

		ObjectNode node = objectMapper.valueToTree(newAuction);
		String jsonContent = node.toString();

		mockMvc.perform(
				post("/api/auctions").contentType(MediaType.APPLICATION_JSON).content(jsonContent).with(jwtAndCsrf()))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/api/auctions/")))
				.andExpect(jsonPath("$.price").value("111.11")).andExpect(jsonPath("$.productQuantity").value("11"))
				.andExpect(jsonPath("$.active").value("true"))
				.andExpect(jsonPath("$.strategy.id").value(getTestAuctionStrategy().getId()));

		Auction createdAuction = auctionRepository.findAll().stream()
				.filter(auction -> auction.getPrice().equals(new BigDecimal("111.11"))).findFirst()
				.orElseThrow(() -> new AssertionError("Enchère non trouvée"));
	}

	/**
	 * Teste la récupération de la liste de tous les enchères.
	 * 
	 */
	@Test
	public void testListAuctions() throws Exception {
		mockMvc.perform(get("/api/auctions").accept(MediaType.APPLICATION_JSON).with(jwtAndCsrf()))
				.andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
	}

	/**
	 * Teste la mise à jour d'un enchère.
	 * 
	 */
	@Test
	public void testUpdateAuction() throws Exception {
		AuctionStrategyDto strategyDto = new AuctionStrategyDto();
		strategyDto.setId(getTestAuctionStrategy().getId());

		ProductDto productDto = new HarvestProductDto();
		productDto.setId(getTestHarvestProduct().getId());

		AuctionDto updateAuction = new AuctionDto();
		updateAuction.setPrice(new BigDecimal("999.99"));
		updateAuction.setProductQuantity(99);
		updateAuction.setActive(true);
		updateAuction.setCreationDate(LocalDateTime.now());
		updateAuction.setExpirationDate(LocalDateTime.now());
		updateAuction.setStrategy(strategyDto);
		updateAuction.setProduct(productDto);

		ObjectNode node = objectMapper.valueToTree(updateAuction);
		String jsonContent = node.toString();

		mockMvc.perform(put("/api/auctions/" + getTestAuction().getId()).contentType(MediaType.APPLICATION_JSON)
				.content(jsonContent).with(jwtAndCsrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.productQuantity").value("99"));
	}

	/**
	 * Teste la suppression d'un enchère.
	 * 
	 */
	@Test
	public void testDeleteAuction() throws Exception {
		// TODO delete
		// mockMvc.perform(delete("/api/auctions/" + getTestAuction().getId()).with(jwtAndCsrf()))
		// .andExpect(status().isNoContent());
		//
		// mockMvc.perform(get("/api/auctions/" +
		// getTestAuction().getId()).with(jwtAndCsrf())).andExpect(status().isNotFound());
	}
}
