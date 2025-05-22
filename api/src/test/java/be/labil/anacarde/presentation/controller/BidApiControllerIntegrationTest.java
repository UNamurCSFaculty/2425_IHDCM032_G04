package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.write.BidUpdateDto;
import be.labil.anacarde.domain.model.Bid;
import be.labil.anacarde.infrastructure.persistence.BidRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

/** Tests d'intégration pour le contrôleur des offres. */
public class BidApiControllerIntegrationTest extends AbstractIntegrationTest {

	private @Autowired ObjectMapper objectMapper;
	private @Autowired BidRepository bidRepository;

	/**
	 * Teste la récupération d'une offre existant.
	 * 
	 */
	@Test
	public void testGetBid() throws Exception {
		mockMvc.perform(
				get("/api/bids/" + getTestBid().getId())
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.amount").value("10.0"))
				.andExpect(jsonPath("$.auctionId").value(getTestAuction().getId()))
				.andExpect(jsonPath("$.trader.id").value(getProducerTestUser().getId()));
	}

	/**
	 * Teste la création d'une nouvelle offre.
	 * 
	 */
	@Test
	public void testCreateBid() throws Exception {
		BidUpdateDto newBid = new BidUpdateDto();
		newBid.setAmount(new BigDecimal("999.99"));
		newBid.setStatusId(getTestTradeStatus().getId());
		newBid.setCreationDate(LocalDateTime.now());
		newBid.setTraderId(getProducerTestUser().getId());
		newBid.setAuctionId(getTestAuction().getId());

		ObjectNode node = objectMapper.valueToTree(newBid);
		String jsonContent = node.toString();

		mockMvc.perform(post("/api/bids")
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isCreated())

				.andExpect(header().string("Location",
						containsString("/api/bids/")))
				.andExpect(jsonPath("$.amount").value("999.99"))
				.andExpect(jsonPath("$.amount").value("999.99"))
				.andExpect(jsonPath("$.status.id").value(getTestTradeStatus().getId()));

		Bid createdBid = bidRepository.findAll().stream()
				.filter(bid -> bid.getAmount().equals(new BigDecimal("999.99"))).findFirst()
				.orElseThrow(() -> new AssertionError("Offre non trouvée"));
	}

	/**
	 * Teste la création d'une nouvelle enchère, avec un status par défaut.
	 *
	 */
	@Test
	public void testCreateBidWithDefaultStatus() throws Exception {
		BidUpdateDto newBid = new BidUpdateDto();
		newBid.setAmount(new BigDecimal("555.55"));
		newBid.setCreationDate(LocalDateTime.now());
		newBid.setTraderId(getProducerTestUser().getId());
		newBid.setAuctionId(getTestAuction().getId());

		ObjectNode node = objectMapper.valueToTree(newBid);
		String jsonContent = node.toString();

		mockMvc.perform(post("/api/bids")
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent).with(jwtAndCsrf()))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location",
						containsString("/api/bids/")))
				.andExpect(jsonPath("$.amount").value("555.55"));

		Bid createdBid = bidRepository.findAll().stream()
				.filter(bid -> bid.getAmount().equals(new BigDecimal("555.55"))).findFirst()
				.orElseThrow(() -> new AssertionError("Offre non trouvée"));
	}

	/**
	 * Teste la récupération de la liste de tous les offres.
	 * 
	 */
	@Test
	public void testListBids() throws Exception {
		mockMvc.perform(get("/api/bids")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(2));
	}

	@Test
	public void testListBidsByAuctionId() throws Exception {
		mockMvc.perform(get("/api/bids?auctionId=" + getTestAuction().getId())
						.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(1));
	}

	/**
	 * Teste la mise à jour d'une offre.
	 * 
	 */
	@Test
	public void testUpdateBid() throws Exception {
		BidUpdateDto updateBid = new BidUpdateDto();
		updateBid.setAmount(new BigDecimal("1234567.01"));
		updateBid.setAuctionId(getTestAuction().getId());
		updateBid.setStatusId(getTestTradeStatus().getId());
		updateBid.setCreationDate(LocalDateTime.now());
		updateBid.setTraderId(getProducerTestUser().getId());
		updateBid.setAuctionId(getTestAuction().getId());

		ObjectNode node = objectMapper.valueToTree(updateBid);
		String jsonContent = node.toString();

		mockMvc.perform(
				put("/api/bids/" + getTestBid().getId())
						.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isOk()).andExpect(jsonPath("$.amount").value("1234567.01"));
	}

	/**
	 * Teste l'acceptation d'une offre.
	 *
	 */
	@Test
	public void testAcceptBid() throws Exception {
		String jsonContent = "";

		mockMvc.perform(
				put("/api/bids/" + getTestBid().getId()
						+ "/accept").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isOk()).andExpect(jsonPath("$.amount").value("10.0"))
				.andExpect(jsonPath("$.status.name").value("Accepté"));
	}

	@Test
	public void testRejectBid() throws Exception {
		String jsonContent = "";

		mockMvc.perform(
				put("/api/bids/" + getTestBid().getId()
						+ "/reject").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isOk()).andExpect(jsonPath("$.amount").value("10.0"))
				.andExpect(jsonPath("$.status.name").value("Refusé"));
	}

	/**
	 * Teste la suppression d'une offre.
	 * 
	 */
	@Test
	public void testDeleteBid() throws Exception {
		mockMvc.perform(delete(
				"/api/bids/" + getTestBid().getId()))
				.andExpect(status().isNoContent());

		mockMvc.perform(
				get("/api/bids/" + getTestBid().getId()))
				.andExpect(status().isNotFound());
	}
}
