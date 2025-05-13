package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.db.AddressDto;
import be.labil.anacarde.domain.dto.db.StoreDetailDto;
import be.labil.anacarde.infrastructure.persistence.StoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Tests d'intégration pour le contrôleur des magasins. */
public class StoreApiControllerIntegrationTest extends AbstractIntegrationTest {

	private @Autowired ObjectMapper objectMapper;
	private @Autowired StoreRepository storeRepository;

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
	 * Teste la récupération d'un magasin existant.
	 * 
	 */
	@Test
	public void testGetStore() throws Exception {
		mockMvc.perform(
				get("/api/stores/" + getMainTestStore().getId()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.address.street").value("Rue de la paix"))
				.andExpect(jsonPath("$.address.cityId").value(1))
				.andExpect(jsonPath("$.address.regionId").value(1));
	}

	/**
	 * Teste la création d'un nouvel magasin.
	 * 
	 */
	@Test
	public void testCreateStore() throws Exception {
		StoreDetailDto newStore = new StoreDetailDto();
		newStore.setName("Nassara");
		newStore.setAddress(AddressDto.builder().street("Rue de la paix").cityId(1).regionId(1)
				.location("POINT(2.3522 48.8566)").build());
		newStore.setUserId(getMainTestUser().getId());

		ObjectNode node = objectMapper.valueToTree(newStore);
		String jsonContent = node.toString();

		mockMvc.perform(
				post("/api/stores").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/api/stores/")))
				.andExpect(jsonPath("$.name").value("Nassara"))
				.andExpect(jsonPath("$.address").exists())
				.andExpect(jsonPath("$.address.street").value("Rue de la paix"))
				.andExpect(jsonPath("$.address.cityId").value(1))
				.andExpect(jsonPath("$.address.regionId").value(1))
				.andExpect(jsonPath("$.userId").value(getMainTestUser().getId()));
	}

	/**
	 * Teste la récupération de la liste de tous les magasins.
	 * 
	 */
	@Test
	public void testListStores() throws Exception {
		mockMvc.perform(get("/api/stores").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
	}

	/**
	 * Teste la mise à jour d'un magasin.
	 * 
	 */
	@Test
	public void testUpdateStore() throws Exception {
		StoreDetailDto updateStore = new StoreDetailDto();
		updateStore.setName("Casimir");
		updateStore.setAddress(
				AddressDto.builder().street("Rue de la paix").cityId(1).regionId(1).build());
		updateStore.setAddress(
				AddressDto.builder().street("Rue de la paix").cityId(1).regionId(1).build());
		updateStore.setUserId(getMainTestUser().getId());

		ObjectNode node = objectMapper.valueToTree(updateStore);
		String jsonContent = node.toString();

		mockMvc.perform(put("/api/stores/" + getMainTestStore().getId())
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Casimir"))
				.andExpect(jsonPath("$.address.street").value("Rue de la paix"))
				.andExpect(jsonPath("$.address.cityId").value(1))
				.andExpect(jsonPath("$.address.regionId").value(1));
	}

	/**
	 * Teste la suppression d'un magasin.
	 * 
	 */
	@Test
	public void testDeleteStore() throws Exception {
		// TODO delete
		// mockMvc.perform(delete("/api/stores/" + getMainTestStore().getId()))
		// .andExpect(status().isNoContent());
		//
		// mockMvc.perform(get("/api/stores/" +
		// getMainTestStore().getId())).andExpect(status().isNotFound());
	}
}
