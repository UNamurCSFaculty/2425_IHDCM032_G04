package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.write.product.HarvestProductUpdateDto;
import be.labil.anacarde.domain.dto.write.product.TransformedProductUpdateDto;
import be.labil.anacarde.domain.model.Product;
import be.labil.anacarde.infrastructure.persistence.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

/** Tests d'intégration pour le contrôleur des produits. */
public class ProductApiControllerIntegrationTest extends AbstractIntegrationTest {

	private @Autowired ObjectMapper objectMapper;
	private @Autowired ProductRepository productRepository;

	/**
	 * Teste la récupération d'un produit existant.
	 * 
	 */
	@Test
	public void testGetHarvestProduct() throws Exception {
		mockMvc.perform(get("/api/products/" + getTestHarvestProduct().getId())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.type").value("harvest"))
				.andExpect(jsonPath("$.store.location").value("POINT (2.3522 48.8566)"))
				.andExpect(jsonPath("$.weightKg").value("2000.0"));
	}

	@Test
	public void testGetTransformedProduct() throws Exception {
		mockMvc.perform(get("/api/products/" + getTestTransformedProduct().getId())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.type").value("transformed"))
				.andExpect(jsonPath("$.identifier").value("XYZ"))
				.andExpect(jsonPath("$.weightKg").value("2000.0"));
	}

	/**
	 * Teste la création d'un nouveau produit de récolte.
	 * 
	 */
	@Test
	public void testCreateHarvestProduct() throws Exception {

		Integer producerId = getProducerTestUser().getId();
		Integer fieldId = getMainTestField().getId();
		Integer storeId = getMainTestStore().getId();

		HarvestProductUpdateDto newProduct = new HarvestProductUpdateDto();
		newProduct.setProducerId(producerId);
		newProduct.setStoreId(storeId);
		newProduct.setWeightKg(200.0);
		newProduct.setDeliveryDate(LocalDateTime.now().plusMonths(1));
		newProduct.setFieldId(fieldId);

		ObjectNode node = objectMapper.valueToTree(newProduct);
		String jsonContent = node.toString();

		mockMvc.perform(
				post("/api/products").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/api/products/")))
				.andExpect(jsonPath("$.type").value("harvest"))
				.andExpect(jsonPath("$.weightKg").value("200.0"))
				.andExpect(jsonPath("$.producer.id").value(producerId));

		Product createdProduct = productRepository.findAll().stream()
				.filter(product -> product.getWeightKg().equals(200.0)).findFirst()
				.orElseThrow(() -> new AssertionError("Product non trouvé"));
	}

	/**
	 * Teste la création d'un nouveau produit transformé.
	 *
	 */
	@Test
	public void testCreateTransformedProduct() throws Exception {
		Integer storeId = getMainTestStore().getId();
		Integer transformerId = getTransformerTestUser().getId();

		TransformedProductUpdateDto newProduct = new TransformedProductUpdateDto();
		newProduct.setTransformerId(transformerId);
		newProduct.setWeightKg(1234567.0);
		newProduct.setIdentifier("TP001");
		newProduct.setDeliveryDate(LocalDateTime.now().minusDays(1));
		newProduct.setStoreId(storeId);

		ObjectNode node = objectMapper.valueToTree(newProduct);
		String jsonContent = node.toString();

		mockMvc.perform(
				post("/api/products").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/api/products/")))
				.andExpect(jsonPath("$.type").value("transformed"))
				.andExpect(jsonPath("$.weightKg").value("1234567.0"))
				.andExpect(jsonPath("$.transformer.id").value(transformerId));

		Product createdProduct = productRepository.findAll().stream()
				.filter(product -> product.getWeightKg().equals(1234567.0)).findFirst()
				.orElseThrow(() -> new AssertionError("Product non trouvé"));
	}

	/**
	 * Teste la récupération de la liste de tous les produits.
	 * 
	 */
	@Test
	public void testListProducts() throws Exception {
		mockMvc.perform(get("/api/products").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(2));
	}

	/**
	 * Teste la récupération de la liste de tous les produits par utilisateur.
	 *
	 */
	@Test
	public void testListProductsByTrader() throws Exception {
		mockMvc.perform(get("/api/products?traderId=" + getTransformerTestUser().getId())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].type").value("transformed"));
	}

	/**
	 * Teste la mise à jour d'un produit.
	 * 
	 */
	@Test
	public void testUpdateProduct() throws Exception {
		// ProductDto updateProduct = new ProductDto();
		// updateProduct.setLocation("POINT (1.111 2.222)");
		// updateProduct.setUserId(getMainTestUser().getId());
		//
		// ObjectNode node = objectMapper.valueToTree(updateProduct);
		// String jsonContent = node.toString();
		//
		// mockMvc.perform(put("/api/products/" + getMainTestProduct().getId())
		// .contentType(MediaType.APPLICATION_JSON).content(jsonContent))
		// .andExpect(status().isOk())
		// .andExpect(jsonPath("$.location").value("POINT (1.111 2.222)"));
	}

	/**
	 * Teste la suppression d'un produit de récolte.
	 * 
	 */
	@Test
	public void testDeleteHarvestProduct() throws Exception {
		// TODO delete
		// mockMvc.perform(delete("/api/products/" + getTestHarvestProduct().getId()))
		// .andExpect(status().isNoContent());
		//
		// mockMvc.perform(get("/api/products/" + getTestHarvestProduct().getId()))
		// .andExpect(status().isNotFound());
	}
}
