package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.db.AuctionStrategyDto;
import be.labil.anacarde.domain.dto.db.TradeStatusDto;
import be.labil.anacarde.domain.dto.db.product.HarvestProductDto;
import be.labil.anacarde.domain.dto.db.product.ProductDto;
import be.labil.anacarde.domain.dto.db.user.ProducerDetailDto;
import be.labil.anacarde.domain.dto.write.AuctionOptionsUpdateDto;
import be.labil.anacarde.domain.dto.write.AuctionUpdateDto;
import be.labil.anacarde.domain.model.Auction;
import be.labil.anacarde.infrastructure.persistence.AuctionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

/** Tests d'intégration pour le contrôleur des enchères. */
public class AuctionApiControllerIntegrationTest extends AbstractIntegrationTest {

	private @Autowired ObjectMapper objectMapper;
	private @Autowired AuctionRepository auctionRepository;
	private @Autowired Scheduler scheduler;

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
	 * Teste la récupération des settings d'enchères.
	 *
	 */
	@Test
	public void testGetAuctionSettings() throws Exception {
		mockMvc.perform(get("/api/auctions/settings").accept(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.defaultStrategy.name").value("Meilleure offre"))
				.andExpect(jsonPath("$.minIncrement").value("1"));
	}

	/**
	 * Teste la création d'une nouvelle enchère.
	 *
	 */
	@Test
	public void testCreateAuction() throws Exception {
		AuctionStrategyDto strategyDto = new AuctionStrategyDto();
		strategyDto.setId(getTestAuctionStrategy().getId());
		strategyDto.setName("BestOffer");

		ProducerDetailDto producer = new ProducerDetailDto();
		producer.setId(getProducerTestUser().getId());

		ProductDto productDto = new HarvestProductDto();
		productDto.setId(getTestHarvestProduct().getId());

		TradeStatusDto statusDto = new TradeStatusDto();
		statusDto.setId(getTestTradeStatus().getId());

		AuctionOptionsUpdateDto optionsDto = new AuctionOptionsUpdateDto();
		optionsDto.setStrategyId(getTestAuctionStrategy().getId());
		optionsDto.setBuyNowPrice(100.50);
		optionsDto.setShowPublic(true);

		AuctionUpdateDto newAuction = new AuctionUpdateDto();
		newAuction.setPrice(111.11);
		newAuction.setProductQuantity(11);
		newAuction.setActive(true);
		newAuction.setExpirationDate(LocalDateTime.now().plusDays(1));
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

		// Vérifie que l'enchère a été enregistrée
		Auction createdAuction = auctionRepository.findAll().stream()
				.filter(auction -> auction.getPrice().equals(111.11)).findFirst()
				.orElseThrow(() -> new AssertionError("Enchère non trouvée"));

		// Vérifie que le job Quartz a bien été programmé
		String jobKeyName = "auctionCloseJob-" + createdAuction.getId();
		String group = "auction-jobs";
		JobKey jobKey = new JobKey(jobKeyName, group);
		boolean jobExists = scheduler.checkExists(jobKey);
		assertTrue(jobExists,
				"Le job Quartz pour la clôture de l'enchère n'a pas été trouvé dans le scheduler.");
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
		newAuction.setExpirationDate(LocalDateTime.now().plusDays(1));
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

		// Vérifie que l'enchère a été enregistrée
		Auction createdAuction = auctionRepository.findAll().stream()
				.filter(auction -> auction.getPrice().equals(111.11)).findFirst()
				.orElseThrow(() -> new AssertionError("Enchère non trouvée"));

		// Vérifie que le job Quartz a bien été programmé
		String jobKeyName = "auctionCloseJob-" + createdAuction.getId();
		String group = "auction-jobs";
		JobKey jobKey = new JobKey(jobKeyName, group);
		boolean jobExists = scheduler.checkExists(jobKey);
		assertTrue(jobExists,
				"Le job Quartz pour la clôture de l'enchère n'a pas été trouvé dans le scheduler.");
	}

	/**
	 * Teste la création d'une nouvelle enchère, avec un status par défaut et des options par
	 * défaut.
	 */
	@Test
	public void testCreateAuctionWithDefaultStatusAndDefaultOptions() throws Exception {
		ProducerDetailDto producer = new ProducerDetailDto();
		producer.setId(getProducerTestUser().getId());

		ProductDto productDto = new HarvestProductDto();
		productDto.setId(getTestHarvestProduct().getId());

		AuctionUpdateDto newAuction = new AuctionUpdateDto();
		newAuction.setPrice(111.11);
		newAuction.setProductQuantity(11);
		newAuction.setActive(true);
		newAuction.setExpirationDate(LocalDateTime.now().plusDays(1));
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

		// Vérifie que l'enchère a été enregistrée
		Auction createdAuction = auctionRepository.findAll().stream()
				.filter(auction -> auction.getPrice().equals(111.11)).findFirst()
				.orElseThrow(() -> new AssertionError("Enchère non trouvée"));

		// Vérifie que le job Quartz a bien été programmé
		String jobKeyName = "auctionCloseJob-" + createdAuction.getId();
		String group = "auction-jobs";
		JobKey jobKey = new JobKey(jobKeyName, group);
		boolean jobExists = scheduler.checkExists(jobKey);
		assertTrue(jobExists,
				"Le job Quartz pour la clôture de l'enchère n'a pas été trouvé dans le scheduler.");
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
	 * Teste la création d'une enchère suivie de sa mise à jour avec un changement de statut (vers
	 * "Rejeté") et vérifie que : - le job Quartz est bien créé lors de la création de l'enchère, -
	 * le job Quartz est supprimé après la mise à jour du statut.
	 */
	@Test
	public void testCreateAuctionThenUpdateStatusAndCheckQuartzJobRemoval() throws Exception {
		ProducerDetailDto producer = new ProducerDetailDto();
		producer.setId(getProducerTestUser().getId());

		ProductDto productDto = new HarvestProductDto();
		productDto.setId(getTestHarvestProduct().getId());

		AuctionOptionsUpdateDto optionsDto = new AuctionOptionsUpdateDto();
		optionsDto.setStrategyId(getTestAuctionStrategy().getId());
		optionsDto.setShowPublic(true);

		AuctionUpdateDto newAuction = new AuctionUpdateDto();
		newAuction.setOptions(optionsDto);
		newAuction.setPrice(111.11);
		newAuction.setProductQuantity(11);
		newAuction.setActive(true);
		newAuction.setExpirationDate(LocalDateTime.now().plusDays(1));
		newAuction.setProductId(productDto.getId());
		newAuction.setTraderId(producer.getId());

		ObjectNode node = objectMapper.valueToTree(newAuction);
		String jsonContent = node.toString();

		MvcResult result = mockMvc
				.perform(post("/api/auctions").contentType(MediaType.APPLICATION_JSON)
						.content(jsonContent))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status.name").value("Ouvert")).andReturn();

		Long auctionId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id")
				.asLong();

		// Vérification du job Quartz
		String jobKeyName = "auctionCloseJob-" + auctionId;
		JobKey jobKey = new JobKey(jobKeyName, "auction-jobs");
		assertTrue(scheduler.checkExists(jobKey), "Le job Quartz doit exister après la création.");

		AuctionUpdateDto updateAuction = new AuctionUpdateDto();
		updateAuction.setPrice(999.99);
		updateAuction.setProductQuantity(99);
		updateAuction.setActive(true);
		updateAuction.setExpirationDate(LocalDateTime.now().plusDays(1));
		updateAuction.setOptions(optionsDto);
		updateAuction.setProductId(productDto.getId());
		updateAuction.setTraderId(producer.getId());
		updateAuction.setStatusId(getTradeStatusRejected().getId());

		node = objectMapper.valueToTree(updateAuction);
		jsonContent = node.toString();

		mockMvc.perform(put("/api/auctions/" + auctionId).contentType(MediaType.APPLICATION_JSON)
				.content(jsonContent)).andExpect(status().isOk())
				.andExpect(jsonPath("$.status.id").value(getTradeStatusRejected().getId()));

		// Vérification que le job a été supprimé
		assertFalse(scheduler.checkExists(jobKey),
				"Le job Quartz doit être supprimé après le changement de statut.");
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
	 * Teste la création d'une enchère suivie de son acceptation manuelle, et vérifie que : - le job
	 * Quartz est bien créé après la création de l'enchère, - le job est supprimé après acceptation
	 * de l'enchère, - le statut de l'enchère devient "Accepté".
	 */
	@Test
	public void testCreateThenAcceptAuctionAndCheckQuartzJobRemoval() throws Exception {
		ProducerDetailDto producer = new ProducerDetailDto();
		producer.setId(getProducerTestUser().getId());

		ProductDto productDto = new HarvestProductDto();
		productDto.setId(getTestHarvestProduct().getId());

		AuctionOptionsUpdateDto optionsDto = new AuctionOptionsUpdateDto();
		optionsDto.setStrategyId(getTestAuctionStrategy().getId());
		optionsDto.setShowPublic(true);

		AuctionUpdateDto newAuction = new AuctionUpdateDto();
		newAuction.setOptions(optionsDto);
		newAuction.setPrice(123.45);
		newAuction.setProductQuantity(10);
		newAuction.setActive(true);
		newAuction.setExpirationDate(LocalDateTime.now().plusDays(1));
		newAuction.setProductId(productDto.getId());
		newAuction.setTraderId(producer.getId());

		ObjectNode node = objectMapper.valueToTree(newAuction);
		String jsonContent = node.toString();

		MvcResult result = mockMvc.perform(
				post("/api/auctions").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isCreated()).andReturn();

		Long auctionId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id")
				.asLong();

		// Vérification que le job Quartz est bien créé
		String jobKeyName = "auctionCloseJob-" + auctionId;
		JobKey jobKey = new JobKey(jobKeyName, "auction-jobs");
		assertTrue(scheduler.checkExists(jobKey), "Le job Quartz doit exister après la création.");
		mockMvc.perform(put("/api/auctions/" + auctionId + "/accept")
				.contentType(MediaType.APPLICATION_JSON).content("")).andExpect(status().isOk())
				.andExpect(jsonPath("$.productQuantity").value("10"))
				.andExpect(jsonPath("$.status.name").value("Accepté"));

		// Vérification que le job Quartz a été supprimé
		assertFalse(scheduler.checkExists(jobKey),
				"Le job Quartz doit être supprimé après l'acceptation de l'enchère.");
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

	/**
	 * Teste la création puis la suppression d'une enchère, et vérifie que : - le job Quartz de
	 * clôture est bien créé après la création, - le job Quartz est supprimé après la suppression de
	 * l'enchère, - l'enchère est bien marquée comme inactive (soft delete).
	 */
	@Test
	public void testCreateThenDeleteAuctionAndCheckQuartzJobRemoval() throws Exception {
		ProducerDetailDto producer = new ProducerDetailDto();
		producer.setId(getProducerTestUser().getId());

		ProductDto productDto = new HarvestProductDto();
		productDto.setId(getTestHarvestProduct().getId());

		AuctionOptionsUpdateDto optionsDto = new AuctionOptionsUpdateDto();
		optionsDto.setStrategyId(getTestAuctionStrategy().getId());
		optionsDto.setShowPublic(true);

		AuctionUpdateDto newAuction = new AuctionUpdateDto();
		newAuction.setOptions(optionsDto);
		newAuction.setPrice(123.45);
		newAuction.setProductQuantity(10);
		newAuction.setActive(true);
		newAuction.setExpirationDate(LocalDateTime.now().plusDays(1));
		newAuction.setProductId(productDto.getId());
		newAuction.setTraderId(producer.getId());

		ObjectNode node = objectMapper.valueToTree(newAuction);
		String jsonContent = node.toString();

		MvcResult result = mockMvc.perform(
				post("/api/auctions").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isCreated()).andReturn();

		Long auctionId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id")
				.asLong();

		// Vérification que le job Quartz est bien créé
		String jobKeyName = "auctionCloseJob-" + auctionId;
		JobKey jobKey = new JobKey(jobKeyName, "auction-jobs");
		assertTrue(scheduler.checkExists(jobKey), "Le job Quartz doit exister après la création.");

		mockMvc.perform(delete("/api/auctions/" + auctionId)).andExpect(status().isNoContent());

		// Vérification que le job Quartz a été supprimé
		assertFalse(scheduler.checkExists(jobKey),
				"Le job Quartz doit être supprimé après suppression de l'enchère.");

		// Vérification que l'enchère est inactive (soft delete)
		mockMvc.perform(get("/api/auctions/" + auctionId).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.active").value(false));
	}

	/**
	 * Teste la création d'une enchère avec expiration automatique rapide et vérifie que : - le job
	 * Quartz est bien créé après la création, - le job est exécuté et supprimé automatiquement
	 * après expiration, - l'enchère passe bien au statut "Expiré" après expiration.
	 */
	@Test
	public void testCreateThenAutoCloseAuctionAndCheckQuartzJobRemovalSimple() throws Exception {
		// --- Création d'une enchère avec expiration rapide ---
		ProducerDetailDto producer = new ProducerDetailDto();
		producer.setId(getProducerTestUser().getId());

		ProductDto productDto = new HarvestProductDto();
		productDto.setId(getTestHarvestProduct().getId());

		AuctionOptionsUpdateDto optionsDto = new AuctionOptionsUpdateDto();
		optionsDto.setStrategyId(getTestAuctionStrategy().getId());
		optionsDto.setShowPublic(true);

		AuctionUpdateDto newAuction = new AuctionUpdateDto();
		newAuction.setOptions(optionsDto);
		newAuction.setPrice(50.0);
		newAuction.setProductQuantity(2);
		newAuction.setActive(true);
		newAuction.setExpirationDate(LocalDateTime.now().plusSeconds(5)); // auto-close rapide
		newAuction.setProductId(productDto.getId());
		newAuction.setTraderId(producer.getId());

		ObjectNode node = objectMapper.valueToTree(newAuction);
		String jsonContent = node.toString();

		MvcResult result = mockMvc.perform(
				post("/api/auctions").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isCreated()).andReturn();

		Long auctionId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id")
				.asLong();
		JobKey jobKey = new JobKey("auctionCloseJob-" + auctionId, "auction-jobs");

		assertTrue(scheduler.checkExists(jobKey), "Le job Quartz doit exister après création.");

		Thread.sleep(11000);

		MvcResult closedResult = mockMvc.perform(get("/api/auctions/" + auctionId))
				.andExpect(status().isOk()).andReturn();

		String statusName = objectMapper.readTree(closedResult.getResponse().getContentAsString())
				.get("status").get("name").asText();

		assertEquals("Expiré", statusName, "Le statut de l'enchère doit être 'Expiré'.");
		assertFalse(scheduler.checkExists(jobKey),
				"Le job Quartz doit être supprimé après clôture.");
	}

}
