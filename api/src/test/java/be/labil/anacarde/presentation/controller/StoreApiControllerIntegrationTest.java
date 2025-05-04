package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.StoreDetailDto;
import be.labil.anacarde.domain.model.Store;
import be.labil.anacarde.infrastructure.persistence.StoreRepository;
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

/** Tests d'intégration pour le contrôleur des magasins. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StoreApiControllerIntegrationTest extends AbstractIntegrationTest {

	private @Autowired MockMvc mockMvc;
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
		mockMvc.perform(get("/api/stores/" + getMainTestStore().getId()).accept(MediaType.APPLICATION_JSON).with(jwt()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.location").value("POINT (2.3522 48.8566)"));
	}

	/**
	 * Teste la création d'un nouvel magasin.
	 * 
	 */
	@Test
	public void testCreateStore() throws Exception {
		StoreDetailDto newStore = new StoreDetailDto();
		newStore.setLocation("POINT(2.3522 48.8566)");
		newStore.setUserId(getMainTestUser().getId());

		ObjectNode node = objectMapper.valueToTree(newStore);
		String jsonContent = node.toString();

		mockMvc.perform(post("/api/stores").contentType(MediaType.APPLICATION_JSON).content(jsonContent).with(jwt()))
				.andExpect(status().isCreated()).andExpect(header().string("Location", containsString("/api/stores/")))
				.andExpect(jsonPath("$.location").value("POINT (2.3522 48.8566)"))
				.andExpect(jsonPath("$.userId").value(getMainTestUser().getId()));

		Store createdStore = storeRepository.findAll().stream()
				.filter(store -> store.getLocation().toText().equals("POINT (2.3522 48.8566)")).findFirst()
				.orElseThrow(() -> new AssertionError("Store non trouvé"));
	}

	/**
	 * Teste la récupération de la liste de tous les magasins.
	 * 
	 */
	@Test
	public void testListStores() throws Exception {
		mockMvc.perform(get("/api/stores").accept(MediaType.APPLICATION_JSON).with(jwt())).andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	/**
	 * Teste la mise à jour d'un magasin.
	 * 
	 */
	@Test
	public void testUpdateStore() throws Exception {
		StoreDetailDto updateStore = new StoreDetailDto();
		updateStore.setLocation("POINT (1.111 2.222)");
		updateStore.setUserId(getMainTestUser().getId());

		ObjectNode node = objectMapper.valueToTree(updateStore);
		String jsonContent = node.toString();

		mockMvc.perform(put("/api/stores/" + getMainTestStore().getId()).contentType(MediaType.APPLICATION_JSON)
				.content(jsonContent).with(jwt())).andExpect(status().isOk())
				.andExpect(jsonPath("$.location").value("POINT (1.111 2.222)"));
	}

	/**
	 * Teste la suppression d'un magasin.
	 * 
	 */
	@Test
	public void testDeleteStore() throws Exception {
		// TODO delete
		// mockMvc.perform(delete("/api/stores/" + getMainTestStore().getId()).with(jwt()))
		// .andExpect(status().isNoContent());
		//
		// mockMvc.perform(get("/api/stores/" +
		// getMainTestStore().getId()).with(jwt())).andExpect(status().isNotFound());
	}
}
