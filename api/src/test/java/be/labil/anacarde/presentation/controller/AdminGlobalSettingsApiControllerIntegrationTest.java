package be.labil.anacarde.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import be.labil.anacarde.domain.dto.write.GlobalSettingsUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Tests d'intégration pour le contrôleur des réglages globaux admin. */
public class AdminGlobalSettingsApiControllerIntegrationTest extends AbstractIntegrationTest {

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * RequestPostProcessor qui ajoute automatiquement le cookie JWT à chaque requête.
	 */
	private RequestPostProcessor jwt() {
		return request -> {
			request.setCookies(getJwtCookie());
			return request;
		};
	}

	/**
	 * RequestPostProcessor pour un utilisateur non-admin (producer).
	 */
	private RequestPostProcessor jwtUser() {
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
	 * Teste la récupération des réglages globaux.
	 */
	@Test
	public void testGetGlobalSettings() throws Exception {
		mockMvc.perform(
				get("/api/admin/global-settings").with(jwt()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.showOnlyActive").exists())
				.andExpect(jsonPath("$.forceBetterBids").exists())
				.andExpect(jsonPath("$.minIncrement").exists());
	}

	/**
	 * Teste la mise à jour des réglages globaux avec des données valides.
	 */
	@Test
	public void testUpdateGlobalSettings_ValidData() throws Exception {
		GlobalSettingsUpdateDto updateDto = new GlobalSettingsUpdateDto();
		updateDto.setDefaultFixedPriceKg(new BigDecimal("7.50"));
		updateDto.setDefaultMaxPriceKg(new BigDecimal("12.00"));
		updateDto.setDefaultMinPriceKg(new BigDecimal("3.00"));
		updateDto.setShowOnlyActive(false);
		updateDto.setForceBetterBids(true);
		updateDto.setMinIncrement(500);

		mockMvc.perform(put("/api/admin/global-settings").with(jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.defaultFixedPriceKg").value(7.50))
				.andExpect(jsonPath("$.defaultMaxPriceKg").value(12.00))
				.andExpect(jsonPath("$.defaultMinPriceKg").value(3.00))
				.andExpect(jsonPath("$.showOnlyActive").value(false))
				.andExpect(jsonPath("$.forceBetterBids").value(true))
				.andExpect(jsonPath("$.minIncrement").value(500));
	}

	/**
	 * Teste la mise à jour avec des données partielles.
	 */
	@Test
	public void testUpdateGlobalSettings_PartialUpdate() throws Exception {
		GlobalSettingsUpdateDto updateDto = new GlobalSettingsUpdateDto();
		updateDto.setShowOnlyActive(true);
		updateDto.setForceBetterBids(false);

		mockMvc.perform(put("/api/admin/global-settings").with(jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.showOnlyActive").value(true))
				.andExpect(jsonPath("$.forceBetterBids").value(false));
	}

	/**
	 * Teste la validation - showOnlyActive null.
	 */
	@Test
	public void testUpdateGlobalSettings_InvalidData_ShowOnlyActiveNull() throws Exception {
		GlobalSettingsUpdateDto updateDto = new GlobalSettingsUpdateDto();
		updateDto.setShowOnlyActive(null);
		updateDto.setForceBetterBids(true);

		mockMvc.perform(put("/api/admin/global-settings").with(jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
				.andExpect(status().isBadRequest());
	}

	/**
	 * Teste la validation - forceBetterBids null.
	 */
	@Test
	public void testUpdateGlobalSettings_InvalidData_ForceBetterBidsNull() throws Exception {
		GlobalSettingsUpdateDto updateDto = new GlobalSettingsUpdateDto();
		updateDto.setShowOnlyActive(true);
		updateDto.setForceBetterBids(null);

		mockMvc.perform(put("/api/admin/global-settings").with(jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
				.andExpect(status().isBadRequest());
	}

	/**
	 * Teste l'accès avec un utilisateur non-admin (devrait être refusé).
	 */
	@Test
	public void testGetGlobalSettings_Forbidden() throws Exception {
		mockMvc.perform(get("/api/admin/global-settings").with(jwtUser())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
	}

	/**
	 * Teste la mise à jour avec un utilisateur non-admin (devrait être refusée).
	 */
	@Test
	public void testUpdateGlobalSettings_Forbidden() throws Exception {
		GlobalSettingsUpdateDto updateDto = new GlobalSettingsUpdateDto();
		updateDto.setShowOnlyActive(true);
		updateDto.setForceBetterBids(false);

		mockMvc.perform(put("/api/admin/global-settings").with(jwtUser())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
				.andExpect(status().isForbidden());
	}
}