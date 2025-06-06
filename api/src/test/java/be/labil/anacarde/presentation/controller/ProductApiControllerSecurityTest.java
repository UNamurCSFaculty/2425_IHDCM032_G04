package be.labil.anacarde.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import be.labil.anacarde.domain.dto.write.product.HarvestProductUpdateDto;
import be.labil.anacarde.domain.dto.write.product.TransformedProductUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Tests de sécurité pour le contrôleur des produits. */
public class ProductApiControllerSecurityTest extends AbstractIntegrationTest {

	private @Autowired ObjectMapper objectMapper;

	@Test
	public void testGetProductByUserShouldSucceed() throws Exception {
		mockMvc.perform(get("/api/products/" + getTestHarvestProduct().getId()).with(jwtProducer())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void testGetAllProductsByUserShouldSucceed() throws Exception {
		mockMvc.perform(get("/api/products").with(jwtProducer()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void testCreateHarvestProductByStoreManagerShouldSucceed() throws Exception {
		final RequestPostProcessor actualUser = jwtStoreManager();

		HarvestProductUpdateDto newProduct = new HarvestProductUpdateDto();
		newProduct.setProducerId(getProducerTestUser().getId());
		newProduct.setStoreId(getMainTestStore().getId());
		newProduct.setWeightKg(200.0);
		newProduct.setDeliveryDate(LocalDateTime.now().plusMonths(1));
		newProduct.setFieldId(getMainTestField().getId());
		newProduct.setQualityControlId(getMainTestQualityControl().getId());

		ObjectNode node = objectMapper.valueToTree(newProduct);
		String jsonContent = node.toString();

		mockMvc.perform(post("/api/products").with(actualUser)
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isCreated());
	}

	@Test
	public void testCreateTransformedByStoreManagerShouldSucceed() throws Exception {
		final RequestPostProcessor actualUser = jwtStoreManager();

		TransformedProductUpdateDto newProduct = new TransformedProductUpdateDto();
		newProduct.setTransformerId(getTransformerTestUser().getId());
		newProduct.setStoreId(getMainTestStore().getId());
		newProduct.setWeightKg(200.0);
		newProduct.setDeliveryDate(LocalDateTime.now().plusMonths(1));
		newProduct.setQualityControlId(getMainTestQualityControl().getId());
		newProduct.setIdentifier("brol");

		ObjectNode node = objectMapper.valueToTree(newProduct);
		String jsonContent = node.toString();

		mockMvc.perform(post("/api/products").with(actualUser)
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isCreated());
	}

	@Test
	public void testUpdateProductByStoreManagerShouldSucceed() throws Exception {
		final Integer expectedUser = getStoreManagerTestUser().getId();
		final RequestPostProcessor actualUser = jwtStoreManager();

		HarvestProductUpdateDto updateProduct = new HarvestProductUpdateDto();
		updateProduct.setProducerId(expectedUser);
		updateProduct.setStoreId(getMainTestStore().getId());
		updateProduct.setWeightKg(200.0);
		updateProduct.setDeliveryDate(LocalDateTime.now().plusMonths(1));
		updateProduct.setFieldId(getMainTestField().getId());
		updateProduct.setQualityControlId(getMainTestQualityControl().getId());

		ObjectNode node = objectMapper.valueToTree(updateProduct);
		String jsonContent = node.toString();

		mockMvc.perform(put("/api/products/" + getTestHarvestProduct().getId()).with(actualUser)
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isOk());
	}

	@Test
	public void testUpdateProductByNotStoreManagerShouldFail() throws Exception {
		final RequestPostProcessor actualUser = jwtCarrier();

		HarvestProductUpdateDto updateProduct = new HarvestProductUpdateDto();
		updateProduct.setProducerId(getProducerTestUser().getId());
		updateProduct.setStoreId(getMainTestStore().getId());
		updateProduct.setWeightKg(200.0);
		updateProduct.setDeliveryDate(LocalDateTime.now().plusMonths(1));
		updateProduct.setFieldId(getMainTestField().getId());
		updateProduct.setQualityControlId(getMainTestQualityControl().getId());

		ObjectNode node = objectMapper.valueToTree(updateProduct);
		String jsonContent = node.toString();

		mockMvc.perform(put("/api/products/" + getTestHarvestProduct().getId()).with(actualUser)
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isForbidden());
	}

	@Test
	public void testDeleteProductByUserShouldFail() throws Exception {
		final RequestPostProcessor actualUser = jwtProducer();

		mockMvc.perform(delete("/api/products/" + getTestHarvestProduct().getId()).with(actualUser))
				.andExpect(status().isForbidden());
	}
}
