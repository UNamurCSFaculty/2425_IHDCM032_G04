package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.ContractOfferDto;
import be.labil.anacarde.domain.dto.QualityDto;
import be.labil.anacarde.domain.dto.user.ProducerDetailDto;
import be.labil.anacarde.domain.dto.user.TransformerDetailDto;
import be.labil.anacarde.domain.model.ContractOffer;
import be.labil.anacarde.infrastructure.persistence.ContractOfferRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

/** Tests d'intégration pour le contrôleur des contrats. */
public class ContractOfferApiControllerIntegrationTest extends AbstractIntegrationTest {

	private @Autowired ObjectMapper objectMapper;
	private @Autowired ContractOfferRepository contractOfferRepository;

	/**
	 * Teste la récupération d'un contrat existant.
	 * 
	 */
	@Test
	public void testGetContractOffer() throws Exception {
		mockMvc.perform(get("/api/contracts/" + getMainTestContractOffer().getId()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.status").value("Accepted"))
				.andExpect(jsonPath("$.pricePerKg").value("20.0"))
				.andExpect(jsonPath("$.seller.id").value(getProducerTestUser().getId()))
				.andExpect(jsonPath("$.buyer.id").value(getTransformerTestUser().getId()));
	}

	/**
	 * Teste la création d'un nouvel contrat.
	 * 
	 */
	@Test
	public void testCreateContractOffer() throws Exception {
		QualityDto quality = new QualityDto();
		quality.setId(getMainTestQuality().getId());

		ProducerDetailDto producer = new ProducerDetailDto();
		producer.setId(getProducerTestUser().getId());

		TransformerDetailDto transformer = new TransformerDetailDto();
		transformer.setId(getTransformerTestUser().getId());

		ContractOfferDto newContractOffer = new ContractOfferDto();
		newContractOffer.setStatus("Waiting");
		newContractOffer.setPricePerKg(new BigDecimal("999.99"));
		newContractOffer.setCreationDate(LocalDateTime.now());
		newContractOffer.setEndDate(LocalDateTime.now());
		newContractOffer.setQuality(quality);
		newContractOffer.setBuyer(transformer);
		newContractOffer.setSeller(producer);

		ObjectNode node = objectMapper.valueToTree(newContractOffer);
		String jsonContent = node.toString();

		mockMvc.perform(post("/api/contracts").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/api/contracts/")))
				.andExpect(jsonPath("$.pricePerKg").value("999.99")).andExpect(jsonPath("$.status").value("Waiting"))
				.andExpect(jsonPath("$.seller.id").value(producer.getId()))
				.andExpect(jsonPath("$.buyer.id").value(transformer.getId()))
				.andExpect(jsonPath("$.quality.id").value(quality.getId()));

		ContractOffer createdContractOffer = contractOfferRepository.findAll().stream()
				.filter(contractOffer -> contractOffer.getPricePerKg().equals(new BigDecimal("999.99"))).findFirst()
				.orElseThrow(() -> new AssertionError("Contrat non trouvé"));
	}

	/**
	 * Teste la récupération de la liste de tous les contrats.
	 * 
	 */
	@Test
	public void testListContractOffers() throws Exception {
		mockMvc.perform(get("/api/contracts").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	/**
	 * Teste la mise à jour d'un contrat.
	 * 
	 */
	@Test
	public void testUpdateContractOffer() throws Exception {
		QualityDto quality = new QualityDto();
		quality.setId(getMainTestQuality().getId());

		ProducerDetailDto producer = new ProducerDetailDto();
		producer.setId(getProducerTestUser().getId());

		TransformerDetailDto transformer = new TransformerDetailDto();
		transformer.setId(getTransformerTestUser().getId());

		ContractOfferDto updateContractOffer = new ContractOfferDto();
		updateContractOffer.setSeller(producer);
		updateContractOffer.setBuyer(transformer);
		updateContractOffer.setQuality(quality);
		updateContractOffer.setStatus("Refused");
		updateContractOffer.setPricePerKg(new BigDecimal("555.55"));
		updateContractOffer.setCreationDate(LocalDateTime.now());
		updateContractOffer.setEndDate(LocalDateTime.now());

		ObjectNode node = objectMapper.valueToTree(updateContractOffer);
		String jsonContent = node.toString();

		mockMvc.perform(put("/api/contracts/" + getMainTestContractOffer().getId())
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent)).andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("Refused"));
	}

	/**
	 * Teste la suppression d'un contrat.
	 * 
	 */
	@Test
	public void testDeleteContractOffer() throws Exception {
		mockMvc.perform(delete("/api/contracts/" + getMainTestContractOffer().getId()))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/contracts/" + getMainTestContractOffer().getId())).andExpect(status().isNotFound());
	}
}
