package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.write.ContractOfferUpdateDto;
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
		mockMvc.perform(get("/api/contracts/" + getMainTestContractOffer().getId())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("Accepted"))
				.andExpect(jsonPath("$.pricePerKg").value("20.0"))
				.andExpect(jsonPath("$.seller.id").value(getProducerTestUser().getId()))
				.andExpect(jsonPath("$.buyer.id").value(getTransformerTestUser().getId()));
	}

	/**
	 * Teste la récupération d'un contrat existant via les critères (qualité, vendeur, acheteur).
	 *
	 * @throws Exception
	 *             en cas d'erreur lors de l'exécution de la requête MockMvc.
	 */
	@Test
	public void testGetContractOfferByCriteria() throws Exception {
		mockMvc.perform(get("/api/contracts/by-criteria")
				.param("qualityId", String.valueOf(getMainTestContractOffer().getQuality().getId()))
				.param("sellerId", String.valueOf(getProducerTestUser().getId()))
				.param("buyerId", String.valueOf(getTransformerTestUser().getId()))
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("Accepted"))
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

		Integer qualityId = getMainTestQuality().getId();
		Integer producerId = getProducerTestUser().getId();
		Integer transformerId = getTransformerTestUser().getId();

		ContractOfferUpdateDto newContractOffer = new ContractOfferUpdateDto();
		newContractOffer.setStatus("Waiting");
		newContractOffer.setPricePerKg(new BigDecimal("999.99"));
		newContractOffer.setCreationDate(LocalDateTime.now());
		newContractOffer.setEndDate(LocalDateTime.now());
		newContractOffer.setQualityId(qualityId);
		newContractOffer.setBuyerId(transformerId);
		newContractOffer.setSellerId(producerId);

		ObjectNode node = objectMapper.valueToTree(newContractOffer);
		String jsonContent = node.toString();

		mockMvc.perform(
				post("/api/contracts").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/api/contracts/")))
				.andExpect(jsonPath("$.pricePerKg").value("999.99"))
				.andExpect(jsonPath("$.status").value("Waiting"))
				.andExpect(jsonPath("$.seller.id").value(producerId))
				.andExpect(jsonPath("$.buyer.id").value(transformerId))
				.andExpect(jsonPath("$.quality.id").value(qualityId));

		ContractOffer createdContractOffer = contractOfferRepository.findAll().stream().filter(
				contractOffer -> contractOffer.getPricePerKg().equals(new BigDecimal("999.99")))
				.findFirst().orElseThrow(() -> new AssertionError("Contrat non trouvé"));
	}

	/**
	 * Teste la récupération de la liste de tous les contrats.
	 * 
	 */
	@Test
	public void testListContractOffers() throws Exception {
		mockMvc.perform(get("/api/contracts").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
	}

	/**
	 * Teste la récupération de la liste des contrats d'un utilisateur.
	 *
	 */
	@Test
	public void testListContractOffersByUser() throws Exception {
		mockMvc.perform(get("/api/contracts?traderId=" + getProducerTestUser().getId())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(1));
	}

	/**
	 * Teste la mise à jour d'un contrat.
	 * 
	 */
	@Test
	public void testUpdateContractOffer() throws Exception {

		Integer qualityId = getMainTestQuality().getId();
		Integer producerId = getProducerTestUser().getId();
		Integer transformerId = getTransformerTestUser().getId();

		ContractOfferUpdateDto updateContractOffer = new ContractOfferUpdateDto();
		updateContractOffer.setSellerId(producerId);
		updateContractOffer.setBuyerId(transformerId);
		updateContractOffer.setQualityId(qualityId);
		updateContractOffer.setStatus("Refused");
		updateContractOffer.setPricePerKg(new BigDecimal("555.55"));
		updateContractOffer.setCreationDate(LocalDateTime.now());
		updateContractOffer.setEndDate(LocalDateTime.now());

		ObjectNode node = objectMapper.valueToTree(updateContractOffer);
		String jsonContent = node.toString();

		mockMvc.perform(put("/api/contracts/" + getMainTestContractOffer().getId())
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isOk()).andExpect(jsonPath("$.status").value("Refused"));
	}

	/**
	 * Teste la suppression d'un contrat.
	 * 
	 */
	@Test
	public void testDeleteContractOffer() throws Exception {
		mockMvc.perform(delete("/api/contracts/" + getMainTestContractOffer().getId()))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/contracts/" + getMainTestContractOffer().getId()))
				.andExpect(status().isNotFound());
	}
}
