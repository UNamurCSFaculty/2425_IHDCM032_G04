package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.*;
import be.labil.anacarde.domain.dto.user.ProducerDetailDto;
import be.labil.anacarde.domain.model.Bid;
import be.labil.anacarde.infrastructure.persistence.BidRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Tests d'intégration pour le contrôleur des offres. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BidApiControllerIntegrationTest extends AbstractIntegrationTest {

	private @Autowired MockMvc mockMvc;
	private @Autowired ObjectMapper objectMapper;
	@Autowired
	protected BidRepository bidRepository;

	/**
	 * RequestPostProcessor qui ajoute automatiquement le cookie JWT à chaque requête.
	 *
	 * @return le RequestPostProcessor configuré.
	 */
	private RequestPostProcessor jwt() {
		return request -> {
			request.setCookies(getJwtCookie());
			return request;
		};
	}

	/**
	 * Teste la récupération d'une offre existant.
	 * 
	 */
	@Test
	public void testGetBid() throws Exception {
		mockMvc.perform(get("/api/auctions/" + getTestAuction().getId() + "/bids/" + getTestBid().getId())
				.accept(MediaType.APPLICATION_JSON).with(jwt())).andExpect(status().isOk())
				.andExpect(jsonPath("$.amount").value("10.0"))
				.andExpect(jsonPath("$.auctionId").value(getTestAuction().getId()))
				.andExpect(jsonPath("$.trader.id").value(getProducerTestUser().getId()));
	}

	/**
	 * Teste la création d'une nouvelle offre.
	 * 
	 */
	@Test
	public void testCreateBid() throws Exception {
		ProductDto productDto = new HarvestProductDto();
		productDto.setId(getTestHarvestProduct().getId());

		ProducerDetailDto producer = new ProducerDetailDto();
		producer.setId(getProducerTestUser().getId());

		AuctionDto auctionDto = new AuctionDto();
		auctionDto.setId(getTestAuction().getId());
		auctionDto.setProduct(productDto);

		TradeStatusDto statusDto = new TradeStatusDto();
		statusDto.setId(getTestBidStatus().getId());

		BidDto newBid = new BidDto();
		newBid.setAmount(new BigDecimal("999.99"));
		newBid.setStatus(statusDto);
		newBid.setCreationDate(LocalDateTime.now());
		newBid.setTrader(producer);
		newBid.setAuctionId(auctionDto.getId());

		ObjectNode node = objectMapper.valueToTree(newBid);
		String jsonContent = node.toString();

		mockMvc.perform(post("/api/auctions/" + getTestAuction().getId() + "/bids/")
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent).with(jwt()))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location",
						containsString("/api/auctions/" + getTestAuction().getId() + "/bids/")))
				.andExpect(jsonPath("$.amount").value("999.99")).andExpect(jsonPath("$.amount").value("999.99"))
				.andExpect(jsonPath("$.status.id").value(getTestBidStatus().getId()));

		Bid createdBid = bidRepository.findAll().stream()
				.filter(bid -> bid.getAmount().equals(new BigDecimal("999.99"))).findFirst()
				.orElseThrow(() -> new AssertionError("Offre non trouvée"));
	}

	/**
	 * Teste la récupération de la liste de tous les offres.
	 * 
	 */
	@Test
	public void testListBids() throws Exception {
		mockMvc.perform(get("/api/auctions/" + getTestAuction().getId() + "/bids/").accept(MediaType.APPLICATION_JSON)
				.with(jwt())).andExpect(status().isOk()).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(1));
	}

	/**
	 * Teste la mise à jour d'une offre.
	 * 
	 */
	@Test
	public void testUpdateBid() throws Exception {
		AuctionDto auctionDto = new AuctionDto();
		auctionDto.setId(getTestAuction().getId());

		TradeStatusDto TradeStatusDto = new TradeStatusDto();
		TradeStatusDto.setId(getTestBidStatus().getId());

		ProducerDetailDto producer = new ProducerDetailDto();
		producer.setId(getProducerTestUser().getId());

		BidDto updateBid = new BidDto();
		updateBid.setAmount(new BigDecimal("1234567.01"));
		updateBid.setAuctionId(auctionDto.getId());
		updateBid.setStatus(TradeStatusDto);
		updateBid.setCreationDate(LocalDateTime.now());
		updateBid.setTrader(producer);
		updateBid.setAuctionId(auctionDto.getId());

		ObjectNode node = objectMapper.valueToTree(updateBid);
		String jsonContent = node.toString();

		mockMvc.perform(put("/api/auctions/" + getTestAuction().getId() + "/bids/" + getTestBid().getId())
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent).with(jwt())).andExpect(status().isOk())
				.andExpect(jsonPath("$.amount").value("1234567.01"));
	}

	/**
	 * Teste l'acceptation d'une offre.
	 *
	 */
	@Test
	public void testAcceptBid() throws Exception {
		String jsonContent = "";

		mockMvc.perform(put("/api/auctions/" + getTestAuction().getId() + "/bids/" + getTestBid().getId() + "/accept")
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent).with(jwt())).andExpect(status().isOk())
				.andExpect(jsonPath("$.amount").value("10.0")).andExpect(jsonPath("$.status.name").value("Accepté"));
	}

	/**
	 * Teste la suppression d'une offre.
	 * 
	 */
	@Test
	public void testDeleteBid() throws Exception {
		mockMvc.perform(
				delete("/api/auctions/" + getTestAuction().getId() + "/bids/" + getTestBid().getId()).with(jwt()))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/auctions/" + getTestAuction().getId() + "/bids/" + getTestBid().getId()).with(jwt()))
				.andExpect(status().isNotFound());
	}
}
