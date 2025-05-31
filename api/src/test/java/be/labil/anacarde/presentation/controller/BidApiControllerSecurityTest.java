package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.dto.write.BidUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Tests de sécurité pour le contrôleur des offres. */
public class BidApiControllerSecurityTest extends AbstractIntegrationTest {

	private @Autowired ObjectMapper objectMapper;

	@Test
	public void testGetBidByUserShouldSucceed() throws Exception {
		mockMvc.perform(get("/api/bids/" + getTestBid().getId()).with(jwtProducer())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void testGetAllBidsByUserShouldSucceed() throws Exception {
		mockMvc.perform(get("/api/bids").with(jwtProducer()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void testCreateBidForSameUserShouldSucceed() throws Exception {
		final Integer expectedUser = getProducerTestUser().getId();
		final RequestPostProcessor actualUser = jwtProducer();

		BidUpdateDto newBid = new BidUpdateDto();
		newBid.setAmount(new BigDecimal("999.99"));
		newBid.setStatusId(getTestTradeStatus().getId());
		newBid.setCreationDate(LocalDateTime.now());
		newBid.setTraderId(expectedUser);
		newBid.setAuctionId(getTestAuction().getId());

		ObjectNode node = objectMapper.valueToTree(newBid);
		String jsonContent = node.toString();

		mockMvc.perform(post("/api/bids").with(actualUser)
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isCreated());
	}

	@Test
	public void testCreateBidForAnotherUserShouldFail() throws Exception {
		final Integer expectedUser = getProducerTestUser().getId();
		final RequestPostProcessor actualUser = jwtTransformer();

		BidUpdateDto newBid = new BidUpdateDto();
		newBid.setAmount(new BigDecimal("999.99"));
		newBid.setStatusId(getTestTradeStatus().getId());
		newBid.setCreationDate(LocalDateTime.now());
		newBid.setTraderId(expectedUser);
		newBid.setAuctionId(getTestAuction().getId());

		ObjectNode node = objectMapper.valueToTree(newBid);
		String jsonContent = node.toString();

		mockMvc.perform(post("/api/bids").with(actualUser)
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void testUpdateBidForSameUserShouldFail() throws Exception {
		final Integer expectedUser = getProducerTestUser().getId();
		final RequestPostProcessor actualUser = jwtProducer();

		BidUpdateDto updateBid = new BidUpdateDto();
		updateBid.setAmount(new BigDecimal("1234567.01"));
		updateBid.setStatusId(getTestTradeStatus().getId());
		updateBid.setCreationDate(LocalDateTime.now());
		updateBid.setTraderId(expectedUser);
		updateBid.setAuctionId(getTestAuction().getId());

		ObjectNode node = objectMapper.valueToTree(updateBid);
		String jsonContent = node.toString();

		mockMvc.perform(put("/api/bids/" + getTestBid().getId()).with(actualUser)
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isOk());
	}

	@Test
	public void testUpdateBidForAnotherUserShouldFail() throws Exception {
		final Integer expectedUser = getProducerTestUser().getId();
		final RequestPostProcessor actualUser = jwtTransformer();

		BidUpdateDto updateBid = new BidUpdateDto();
		updateBid.setAmount(new BigDecimal("1234567.01"));
		updateBid.setStatusId(getTestTradeStatus().getId());
		updateBid.setCreationDate(LocalDateTime.now());
		updateBid.setTraderId(expectedUser);
		updateBid.setAuctionId(getTestAuction().getId());

		ObjectNode node = objectMapper.valueToTree(updateBid);
		String jsonContent = node.toString();

		mockMvc.perform(put("/api/bids/" + getTestBid().getId()).with(actualUser)
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void testDeleteBidForSameUserShouldSucceed() throws Exception {
		// expectedUser = producer
		final RequestPostProcessor actualUser = jwtProducer();

		mockMvc.perform(delete("/api/bids/" + getTestBid().getId()).with(actualUser))
				.andExpect(status().isNoContent());
	}

	@Test
	public void testDeleteBidForAnotherUserShouldFail() throws Exception {
		// expectedUser = producer
		final RequestPostProcessor actualUser = jwtCarrier();

		mockMvc.perform(delete("/api/bids/" + getTestBid().getId()).with(actualUser))
				.andExpect(status().is4xxClientError());
	}
}
