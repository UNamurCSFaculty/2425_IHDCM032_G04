package be.labil.anacarde.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.write.AuctionOptionsUpdateDto;
import be.labil.anacarde.domain.dto.write.AuctionUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Tests de sécurité pour le contrôleur des enchères. */
public class AuctionApiControllerSecurityTest extends AbstractIntegrationTest {

	private @Autowired ObjectMapper objectMapper;

	@Test
	public void testGetAuctionByUserShouldSucceed() throws Exception {
		mockMvc.perform(get("/api/auctions/" + getTestAuction().getId()).with(jwtProducer())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void testGetAllAuctionsByUserShouldSucceed() throws Exception {
		mockMvc.perform(get("/api/auctions").with(jwtProducer()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void testCreateAuctionForSameUserShouldSucceed() throws Exception {
		final Integer expectedUser = getProducerTestUser().getId();
		final RequestPostProcessor actualUser = jwtProducer();

		AuctionUpdateDto newAuction = new AuctionUpdateDto();
		newAuction.setPrice(111.11);
		newAuction.setProductQuantity(10);
		newAuction.setActive(true);
		newAuction.setExpirationDate(LocalDateTime.now().plusDays(1));
		newAuction.setProductId(getTestHarvestProduct().getId());
		newAuction.setTraderId(expectedUser);

		ObjectNode node = objectMapper.valueToTree(newAuction);
		String jsonContent = node.toString();

		mockMvc.perform(post("/api/auctions").with(actualUser)
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isCreated());
	}

	@Test
	public void testCreateAuctionForAnotherUserShouldFail() throws Exception {
		final Integer expectedUser = getProducerTestUser().getId();
		final RequestPostProcessor actualUser = jwtTransformer();

		AuctionUpdateDto newAuction = new AuctionUpdateDto();
		newAuction.setPrice(111.11);
		newAuction.setProductQuantity(10);
		newAuction.setActive(true);
		newAuction.setExpirationDate(LocalDateTime.now().plusDays(1));
		newAuction.setProductId(getTestHarvestProduct().getId());
		newAuction.setTraderId(expectedUser);

		ObjectNode node = objectMapper.valueToTree(newAuction);
		String jsonContent = node.toString();

		mockMvc.perform(post("/api/auctions").with(actualUser)
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void testUpdateAuctionForSameUserShouldSucceed() throws Exception {
		final Integer expectedUser = getProducerTestUser().getId();
		final RequestPostProcessor actualUser = jwtProducer();

		AuctionOptionsUpdateDto optionsDto = new AuctionOptionsUpdateDto();
		optionsDto.setStrategyId(getTestAuctionStrategy().getId());
		optionsDto.setBuyNowPrice(100.50);
		optionsDto.setShowPublic(true);

		AuctionUpdateDto updateAuction = new AuctionUpdateDto();
		updateAuction.setPrice(999.99);
		updateAuction.setProductQuantity(99);
		updateAuction.setActive(true);
		updateAuction.setExpirationDate(LocalDateTime.now());
		updateAuction.setOptions(optionsDto);
		updateAuction.setProductId(getTestHarvestProduct().getId());
		updateAuction.setTraderId(expectedUser);
		updateAuction.setStatusId(getTestTradeStatus().getId());

		ObjectNode node = objectMapper.valueToTree(updateAuction);
		String jsonContent = node.toString();

		mockMvc.perform(put("/api/auctions/" + getTestAuction().getId()).with(actualUser)
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isOk());
	}

	@Test
	public void testUpdateAuctionForAnotherUserShouldFail() throws Exception {
		final Integer expectedUser = getProducerTestUser().getId();
		final RequestPostProcessor actualUser = jwtTransformer();

		AuctionOptionsUpdateDto optionsDto = new AuctionOptionsUpdateDto();
		optionsDto.setStrategyId(getTestAuctionStrategy().getId());
		optionsDto.setBuyNowPrice(100.50);
		optionsDto.setShowPublic(true);

		AuctionUpdateDto updateAuction = new AuctionUpdateDto();
		updateAuction.setPrice(999.99);
		updateAuction.setProductQuantity(99);
		updateAuction.setActive(true);
		updateAuction.setExpirationDate(LocalDateTime.now());
		updateAuction.setOptions(optionsDto);
		updateAuction.setProductId(getTestHarvestProduct().getId());
		updateAuction.setTraderId(expectedUser);
		updateAuction.setStatusId(getTestTradeStatus().getId());

		ObjectNode node = objectMapper.valueToTree(updateAuction);
		String jsonContent = node.toString();

		mockMvc.perform(put("/api/auctions/" + getTestAuction().getId()).with(actualUser)
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void testDeleteAuctionForSameUserShouldSucceed() throws Exception {
		// expectedUser = producer
		final RequestPostProcessor actualUser = jwtProducer();

		mockMvc.perform(delete("/api/auctions/" + getTestAuction().getId()).with(actualUser))
				.andExpect(status().isNoContent());
	}

	@Test
	public void testDeleteAuctionForAnotherUserShouldFail() throws Exception {
		// expectedUser = producer
		final RequestPostProcessor actualUser = jwtCarrier();

		mockMvc.perform(delete("/api/auctions/" + getTestAuction().getId()).with(actualUser))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void testAcceptAuctionForSameUserShouldSucceed() throws Exception {
		// expectedUser = producer
		final RequestPostProcessor actualUser = jwtProducer();

		String jsonContent = "";

		mockMvc.perform(put("/api/auctions/" + getTestAuction().getId() + "/accept")
				.with(actualUser).contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isOk());
	}

	@Test
	public void testAcceptAuctionForAnotherUserShouldFail() throws Exception {
		// expectedUser = producer
		final RequestPostProcessor actualUser = jwtTransformer();

		String jsonContent = "";

		mockMvc.perform(put("/api/auctions/" + getTestAuction().getId() + "/accept")
				.with(actualUser).contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().is4xxClientError());
	}
}
