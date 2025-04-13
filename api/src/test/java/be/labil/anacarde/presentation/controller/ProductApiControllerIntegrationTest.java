package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.HarvestProductDto;
import be.labil.anacarde.domain.dto.StoreDetailDto;
import be.labil.anacarde.domain.dto.user.ProducerDetailDto;
import be.labil.anacarde.domain.mapper.ProductMapper;
import be.labil.anacarde.domain.model.Product;
import be.labil.anacarde.infrastructure.persistence.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDateTime;

/** Tests d'intégration pour le contrôleur des produits. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductApiControllerIntegrationTest extends AbstractIntegrationTest {

	private @Autowired MockMvc mockMvc;
	private @Autowired ObjectMapper objectMapper;
	@Autowired
	protected ProductRepository productRepository;
	@Autowired
	private ProductMapper productMapper;

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
	 * Teste la récupération d'un produit existant.
	 * 
	 */
	@Test
	public void testGetProduct() throws Exception {
		// mockMvc.perform(get("/api/products/" + getMainTestProduct().getId())
		// .accept(MediaType.APPLICATION_JSON).with(jwt()))
		// .andExpect(status().isOk()).andDo(print())
		// .andExpect(jsonPath("$.location").value("POINT (2.3522 48.8566)"));
	}

	/**
	 * Teste la création d'un nouveau produit.
	 * 
	 */
	@Test
	public void testCreateProduct() throws Exception {
		// TODO return dto directly from AbstractIntegrationTest
		ProducerDetailDto newProducer = new ProducerDetailDto();
		newProducer.setId(getProducerTestUser().getId());

		// TODO return dto directly from AbstractIntegrationTest
		StoreDetailDto newStore = new StoreDetailDto();
		newStore.setId(getMainTestStore().getId());

		HarvestProductDto newProduct = new HarvestProductDto();
		newProduct.setProducer(newProducer);
		newProduct.setStore(newStore);
		newProduct.setWeightKg(200.0);
		newProduct.setDeliveryDate(LocalDateTime.now().plusMonths(1));

		ObjectNode node = objectMapper.valueToTree(newProduct);
		String jsonContent = node.toString();

		mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(jsonContent).with(jwt()))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/api/products/")))
				.andExpect(jsonPath("$.location").value("POINT (2.3522 48.8566)"))
				.andExpect(jsonPath("$.userId").value(getMainTestUser().getId()));

		Product createdProduct = productRepository.findAll().stream()
				.filter(product -> product.getWeightKg().equals("1800")).findFirst()
				.orElseThrow(() -> new AssertionError("Product non trouvé"));
	}

	/**
	 * Teste la récupération de la liste de tous les produits.
	 * 
	 */
	@Test
	public void testListProducts() throws Exception {
		// mockMvc.perform(get("/api/products").accept(MediaType.APPLICATION_JSON).with(jwt()))
		// .andExpect(status().isOk())
		// .andDo(print())
		// .andExpect(jsonPath("$").isArray());
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
		// .contentType(MediaType.APPLICATION_JSON).content(jsonContent).with(jwt()))
		// .andExpect(status().isOk())
		// .andExpect(jsonPath("$.location").value("POINT (1.111 2.222)"));
	}

	/**
	 * Teste la suppression d'un produit.
	 * 
	 */
	@Test
	public void testDeleteProduct() throws Exception {
		// mockMvc.perform(delete("/api/products/" + getMainTestProduct().getId()).with(jwt()))
		// .andExpect(status().isNoContent());
		//
		// mockMvc.perform(get("/api/products/" +
		// getMainTestProduct().getId()).with(jwt())).andExpect(status().isNotFound());
	}
}
