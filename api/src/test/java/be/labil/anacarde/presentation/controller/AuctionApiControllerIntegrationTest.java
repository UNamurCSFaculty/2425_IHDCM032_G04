package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.db.AuctionStrategyDto;
import be.labil.anacarde.domain.dto.db.TradeStatusDto;
import be.labil.anacarde.domain.dto.db.product.HarvestProductDto;
import be.labil.anacarde.domain.dto.db.product.ProductDto;
import be.labil.anacarde.domain.dto.db.user.ProducerDetailDto;
import be.labil.anacarde.domain.dto.write.AuctionOptionsUpdateDto;
import be.labil.anacarde.domain.dto.write.AuctionUpdateDto;
import be.labil.anacarde.infrastructure.persistence.AuctionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

/** Tests d'intégration pour le contrôleur des enchères. */
public class AuctionApiControllerIntegrationTest extends AbstractIntegrationTest {

	private @Autowired ObjectMapper objectMapper;
	private @Autowired AuctionRepository auctionRepository;

	/**
	 * Teste la récupération d'une enchère existante.
	 * 
	 */
	@Test
	public void testGetAuction() throws Exception {
		mockMvc.perform(
				get("/api/auctions/" + getTestAuction().getId()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.price").value("500.0"))
				.andExpect(jsonPath("$.productQuantity").value("10"))
				.andExpect(jsonPath("$.active").value("true"))
				.andExpect(jsonPath("$.product.weightKg").value("2000.0"))
				.andExpect(jsonPath("$.bids").isArray())
				.andExpect(jsonPath("$.bids.length()").value(1));
	}

	/**
	 * Teste la création d'une nouvelle enchère.
	 * 
	 */
	@Test
	public void testCreateAuction() throws Exception {
		AuctionStrategyDto strategyDto = new AuctionStrategyDto();
		strategyDto.setId(getTestAuctionStrategy().getId());

		ProducerDetailDto producer = new ProducerDetailDto();
		producer.setId(getProducerTestUser().getId());

		ProductDto productDto = new HarvestProductDto();
		productDto.setId(getTestHarvestProduct().getId());

		TradeStatusDto statusDto = new TradeStatusDto();
		statusDto.setId(getTestTradeStatus().getId());

		AuctionOptionsUpdateDto optionsDto = new AuctionOptionsUpdateDto();
		strategyDto.setName("BestOffer");
		optionsDto.setStrategyId(getTestAuctionStrategy().getId());
		optionsDto.setBuyNowPrice(100.50);
		optionsDto.setShowPublic(true);

		AuctionUpdateDto newAuction = new AuctionUpdateDto();
		newAuction.setPrice(111.11);
		newAuction.setProductQuantity(11);
		newAuction.setActive(true);
		newAuction.setCreationDate(LocalDateTime.now());
		newAuction.setExpirationDate(LocalDateTime.now());
		newAuction.setOptions(optionsDto);
		newAuction.setProductId(productDto.getId());
		newAuction.setTraderId(producer.getId());
		newAuction.setStatusId(statusDto.getId());

		ObjectNode node = objectMapper.valueToTree(newAuction);
		String jsonContent = node.toString();

		mockMvc.perform(
				post("/api/auctions").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/api/auctions/")))
				.andExpect(jsonPath("$.price").value("111.11"))
				.andExpect(jsonPath("$.productQuantity").value("11"))
				.andExpect(jsonPath("$.active").value("true"))
				.andExpect(
						jsonPath("$.options.strategy.id").value(getTestAuctionStrategy().getId()))
				.andExpect(jsonPath("$.trader.id").value(getProducerTestUser().getId()));

		auctionRepository.findAll().stream().filter(auction -> auction.getPrice().equals(111.11))
				.findFirst().orElseThrow(() -> new AssertionError("Enchère non trouvée"));
	}

	/**
	 * Teste la création d'une nouvelle enchère, avec un status par défaut.
	 */
	@Test
	public void testCreateAuctionWithDefaultStatus() throws Exception {
		ProducerDetailDto producer = new ProducerDetailDto();
		producer.setId(getProducerTestUser().getId());

		ProductDto productDto = new HarvestProductDto();
		productDto.setId(getTestHarvestProduct().getId());

		AuctionOptionsUpdateDto optionsDto = new AuctionOptionsUpdateDto();
		optionsDto.setStrategyId(getTestAuctionStrategy().getId());
		optionsDto.setBuyNowPrice(100.50);
		optionsDto.setShowPublic(true);

		AuctionUpdateDto newAuction = new AuctionUpdateDto();
		newAuction.setOptions(optionsDto);
		newAuction.setPrice(111.11);
		newAuction.setProductQuantity(11);
		newAuction.setActive(true);
		newAuction.setCreationDate(LocalDateTime.now());
		newAuction.setExpirationDate(LocalDateTime.now());
		newAuction.setProductId(productDto.getId());
		newAuction.setTraderId(producer.getId());

		ObjectNode node = objectMapper.valueToTree(newAuction);
		String jsonContent = node.toString();

		mockMvc.perform(
				post("/api/auctions").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/api/auctions/")))
				.andExpect(jsonPath("$.price").value("111.11"))
				.andExpect(jsonPath("$.productQuantity").value("11"))
				.andExpect(jsonPath("$.active").value("true"))
				.andExpect(jsonPath("$.trader.id").value(getProducerTestUser().getId()))
				.andExpect(jsonPath("$.status.name").value("Ouvert"));

		auctionRepository.findAll().stream().filter(auction -> auction.getPrice().equals(111.11))
				.findFirst().orElseThrow(() -> new AssertionError("Enchère non trouvée"));
	}

	/**
	 * Teste la récupération de la liste de toutes les enchères.
	 * 
	 */
	@Test
	public void testListAuctions() throws Exception {
		mockMvc.perform(get("/api/auctions").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(3));
	}

	/**
	 * Teste la récupération de la liste des enchères créées par un utilisateur.
	 *
	 */
	@Test
	public void testListAuctionsByTraderId() throws Exception {
		mockMvc.perform(get("/api/auctions?traderId=" + getProducerTestUser().getId())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(2));
	}

	/**
	 * Teste la récupération de la liste de enchères remportées par un utilisateur.
	 *
	 */
	@Test
	public void testListAuctionsByBuyerId() throws Exception {
		mockMvc.perform(
				get("/api/auctions?buyerId=" + getProducerTestUser().getId() + "&status=Accepté")
						.accept(MediaType.APPLICATION_JSON).with(jwtAndCsrf()))
				.andExpect(status().isOk()).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(1));
	}

	/**
	 * Teste la mise à jour d'une enchère.
	 * 
	 */
	@Test
	public void testUpdateAuction() throws Exception {
		AuctionStrategyDto strategyDto = new AuctionStrategyDto();
		strategyDto.setId(getTestAuctionStrategy().getId());

		ProducerDetailDto producer = new ProducerDetailDto();
		producer.setId(getProducerTestUser().getId());

		ProductDto productDto = new HarvestProductDto();
		productDto.setId(getTestHarvestProduct().getId());

		TradeStatusDto statusDto = new TradeStatusDto();
		statusDto.setId(getTestTradeStatus().getId());

		AuctionOptionsUpdateDto optionsDto = new AuctionOptionsUpdateDto();
		strategyDto.setName("BestOffer");
		optionsDto.setStrategyId(getTestAuctionStrategy().getId());
		optionsDto.setBuyNowPrice(100.50);
		optionsDto.setShowPublic(true);

		AuctionUpdateDto updateAuction = new AuctionUpdateDto();
		updateAuction.setPrice(999.99);
		updateAuction.setProductQuantity(99);
		updateAuction.setActive(true);
		updateAuction.setCreationDate(LocalDateTime.now());
		updateAuction.setExpirationDate(LocalDateTime.now());
		updateAuction.setOptions(optionsDto);
		updateAuction.setProductId(productDto.getId());
		updateAuction.setTraderId(producer.getId());
		updateAuction.setStatusId(statusDto.getId());

		ObjectNode node = objectMapper.valueToTree(updateAuction);
		String jsonContent = node.toString();

		mockMvc.perform(put("/api/auctions/" + getTestAuction().getId())
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isOk()).andExpect(jsonPath("$.productQuantity").value("99"));
	}

	/**
	 * Test l'acceptation d'une enchère.
	 */
	@Test
	public void testAcceptAuction() throws Exception {
		String jsonContent = "";

		mockMvc.perform(put("/api/auctions/" + getTestAuction().getId() + "/accept")
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isOk()).andExpect(jsonPath("$.productQuantity").value("10"))
				.andExpect(jsonPath("$.status.name").value("Accepté"));
	}

	/**
	 * Teste la suppression d'une enchère.
	 * 
	 */
	@Test
	public void testDeleteAuction() throws Exception {
		mockMvc.perform(delete("/api/auctions/" + getTestAuction().getId()))
				.andExpect(status().isNoContent());

		mockMvc.perform(
				get("/api/auctions/" + getTestAuction().getId()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.active").value(false));
	}
}
