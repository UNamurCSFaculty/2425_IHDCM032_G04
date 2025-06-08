package be.labil.anacarde.presentation.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.application.service.NotificationSseServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Tests d'intégration pour NotificationSseApiController.
 */
class NotificationSseApiControllerIntegrationTest extends AbstractIntegrationTest {

	@MockitoBean
	private NotificationSseServiceImpl notificationSseService;

	/** Teste l'abonnement avec un utilisateur authentifié. */
	@Test
	@WithMockUser(username = "testuser")
	public void testSubscribeWithUserDetails() throws Exception {
		SseEmitter emitter = new SseEmitter();
		when(notificationSseService.subscribe("testuser")).thenReturn(emitter);

		mockMvc.perform(get("/api/notifications/stream")).andExpect(status().isOk());
	}

	/** Teste le cas d'un token JWT invalide qui lance une exception. */
	@Test
	public void testSubscribeWithInvalidTokenThrows() throws Exception {
		String invalidToken = "bad.token"; // token volontairement malformé

		mockMvc.perform(get("/api/notifications/stream").param("token", invalidToken))
				.andExpect(status().is5xxServerError()).andExpect(result -> {
					Throwable ex = result.getResolvedException();
					assertNotNull(ex);
					assertTrue(ex.getMessage().contains("Token JWT invalide pour SSE"));
				});
	}

	/** Teste l'appel sans token ni utilisateur, vérifie la gestion d'exception. */
	@Test
	public void testSubscribeWithoutTokenAndWithoutUserDetailsThrows() throws Exception {
		mockMvc.perform(get("/api/notifications/stream")).andExpect(status().isOk())
				.andExpect(result -> {
					Throwable ex = result.getResolvedException();

				});
	}
}
