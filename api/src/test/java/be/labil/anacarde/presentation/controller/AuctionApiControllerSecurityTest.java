package be.labil.anacarde.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.application.service.GlobalSettingsService;
import be.labil.anacarde.domain.dto.write.AuctionOptionsUpdateDto;
import be.labil.anacarde.domain.dto.write.AuctionUpdateDto;
import be.labil.anacarde.domain.mapper.GlobalSettingsMapper;
import be.labil.anacarde.infrastructure.persistence.HarvestProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Tests de sécurité pour le contrôleur des enchères. */
// @SpringBootTest(classes = AnacardeApplication.class)
public class AuctionApiControllerSecurityTest extends AbstractIntegrationTest {

	private @Autowired ObjectMapper objectMapper;
	private @Autowired HarvestProductRepository harvestProductRepository;
	private @Autowired Scheduler scheduler;
	private @Autowired GlobalSettingsService globalSettingsService;
	private @Autowired GlobalSettingsMapper globalSettingsMapper;

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
	public void testUpdateAuctionForSameUserShouldFail() throws Exception {
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

	/**
	 * RequestPostProcessor pour un producer.
	 */
	private RequestPostProcessor jwtProducer() {
		return request -> {
			UserDetails userDetails = userDetailsService
					.loadUserByUsername(getProducerTestUser().getEmail());
			String token = jwtUtil.generateToken(userDetails);
			jakarta.servlet.http.Cookie userCookie = new jakarta.servlet.http.Cookie("jwt", token);
			userCookie.setHttpOnly(true);
			userCookie.setPath("/");
			request.setCookies(userCookie);
			return request;
		};
	}

	/**
	 * RequestPostProcessor pour un transformer.
	 */
	private RequestPostProcessor jwtTransformer() {
		return request -> {
			UserDetails userDetails = userDetailsService
					.loadUserByUsername(getTransformerTestUser().getEmail());
			String token = jwtUtil.generateToken(userDetails);
			jakarta.servlet.http.Cookie userCookie = new jakarta.servlet.http.Cookie("jwt", token);
			userCookie.setHttpOnly(true);
			userCookie.setPath("/");
			request.setCookies(userCookie);
			return request;
		};
	}

	/**
	 * RequestPostProcessor pour un transformer.
	 */
	private RequestPostProcessor jwtCarrier() {
		return request -> {
			UserDetails userDetails = userDetailsService
					.loadUserByUsername(getMainTestCarrier().getEmail());
			String token = jwtUtil.generateToken(userDetails);
			jakarta.servlet.http.Cookie userCookie = new jakarta.servlet.http.Cookie("jwt", token);
			userCookie.setHttpOnly(true);
			userCookie.setPath("/");
			request.setCookies(userCookie);
			return request;
		};
	}
}
