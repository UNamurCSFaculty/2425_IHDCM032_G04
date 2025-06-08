package be.labil.anacarde.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import be.labil.anacarde.domain.dto.write.ContactRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/** Tests d'intégration pour le contrôleur des messages de contact. */
public class ContactApiControllerIntegrationTest extends AbstractIntegrationTest {

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private JavaMailSender mailSender;

	/**
	 * Teste l'envoi d'un message de contact valide.
	 *
	 * Vérifie que l'endpoint retourne un code HTTP 200 (OK) et que le service d'envoi de mail est
	 * bien appelé.
	 */
	@Test
	public void testSendContactMessage_shouldReturn200() throws Exception {
		// Arrange
		ContactRequestDto dto = new ContactRequestDto();
		dto.setName("Jean Dupont");
		dto.setEmail("jean.dupont@example.com");
		dto.setMessage("Bonjour, j’ai une question.");

		// Act & Assert
		mockMvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk());

		// Vérifie que le mail a bien été envoyé
		verify(mailSender).send(any(SimpleMailMessage.class));
	}

	/**
	 * Teste le comportement lorsque la requête est invalide (champ manquant).
	 *
	 * Vérifie que l'endpoint retourne un code HTTP 400 (Bad Request).
	 */
	@Test
	public void testSendContactMessage_invalidInput_shouldReturn400() throws Exception {
		// Arrange : pas de nom dans la requête
		ContactRequestDto dto = new ContactRequestDto();
		dto.setEmail("jean.dupont@example.com");
		dto.setMessage("Message sans nom");

		// Act & Assert
		mockMvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());
	}
}
