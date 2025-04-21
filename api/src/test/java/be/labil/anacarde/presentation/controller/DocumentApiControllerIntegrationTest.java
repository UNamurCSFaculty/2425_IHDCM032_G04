package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.DocumentDto;
import be.labil.anacarde.infrastructure.persistence.DocumentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 * Tests d'intégration pour le contrôleur des documents.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DocumentApiControllerIntegrationTest extends AbstractIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private DocumentRepository documentRepository;

	private RequestPostProcessor jwt() {
		return request -> {
			request.setCookies(getJwtCookie());
			return request;
		};
	}

	/**
	 * Teste la récupération d'un document existant via son ID.
	 */
	@Test
	public void testGetDocument() throws Exception {
		String expectedDate = getMainTestDocument().getUploadDate()
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
		mockMvc.perform(
				get("/api/documents/" + getMainTestDocument().getId()).accept(MediaType.APPLICATION_JSON).with(jwt()))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(jsonPath("$.id").value(getMainTestDocument().getId()))
				.andExpect(jsonPath("$.storagePath").value(getMainTestDocument().getStoragePath()))
				.andExpect(jsonPath("$.userId").value(getMainTestDocument().getUser().getId()))
				.andExpect(jsonPath("$.format").value(getMainTestDocument().getFormat()))
				.andExpect(jsonPath("$.uploadDate").value(startsWith(expectedDate)))
				.andExpect(jsonPath("$.documentType").value(getMainTestDocument().getType()));
	}

	/**
	 * Teste la création d’un nouveau document.
	 */
	@Test
	public void testCreateDocument() throws Exception {
		DocumentDto documentDto = new DocumentDto();
		documentDto.setStoragePath("/test/path/created.pdf");
		documentDto.setUserId(getProducerTestUser().getId());
		documentDto.setFormat("PDF");
		documentDto.setUploadDate(getMainTestDocument().getUploadDate());
		documentDto.setDocumentType("SOME_TYPE");

		ObjectNode node = objectMapper.valueToTree(documentDto);
		String jsonContent = node.toString();

		String expectedDate = getMainTestDocument().getUploadDate()
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

		mockMvc.perform(post("/api/documents").contentType(MediaType.APPLICATION_JSON).content(jsonContent).with(jwt()))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/api/documents")))
				.andExpect(jsonPath("$.storagePath").value("/test/path/created.pdf"))
				.andExpect(jsonPath("$.userId").value(getProducerTestUser().getId()))
				.andExpect(jsonPath("$.uploadDate").value(startsWith(expectedDate)))
				.andExpect(jsonPath("$.format").value("PDF")).andExpect(jsonPath("$.documentType").value("SOME_TYPE"));
	}

	/**
	 * Teste la mise à jour d’un document existant.
	 */
	@Test
	public void testUpdateDocument() throws Exception {
		DocumentDto documentDto = new DocumentDto();
		documentDto.setId(getMainTestDocument().getId());
		documentDto.setStoragePath("/updated/path/updated.pdf");
		documentDto.setUserId(getProducerTestUser().getId());
		documentDto.setFormat("UPDATED_FORMAT");
		documentDto.setUploadDate(getMainTestDocument().getUploadDate());
		documentDto.setDocumentType("UPDATED_TYPE");

		ObjectNode node = objectMapper.valueToTree(documentDto);
		String jsonContent = node.toString();
		String expectedDate = getMainTestDocument().getUploadDate()
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

		mockMvc.perform(put("/api/documents/" + getMainTestDocument().getId()).contentType(MediaType.APPLICATION_JSON)
				.content(jsonContent).with(jwt())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(getMainTestDocument().getId()))
				.andExpect(jsonPath("$.storagePath").value("/updated/path/updated.pdf"))
				.andExpect(jsonPath("$.userId").value(getProducerTestUser().getId()))
				.andExpect(jsonPath("$.format").value("UPDATED_FORMAT"))
				.andExpect(jsonPath("$.uploadDate").value(startsWith(expectedDate)))
				.andExpect(jsonPath("$.documentType").value("UPDATED_TYPE"));
	}

	/**
	 * Teste la suppression d’un document existant.
	 */
	@Test
	public void testDeleteDocument() throws Exception {
		mockMvc.perform(delete("/api/documents/" + getMainTestDocument().getId()).with(jwt()))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/documents/" + getMainTestDocument().getId()).with(jwt()))
				.andExpect(status().isNotFound());
	}

	/**
	 * Teste la récupération de tous les documents associés à un utilisateur.
	 */
	@Test
	public void testListDocumentsForAUser() throws Exception {
		mockMvc.perform(get("/api/documents/users/" + getMainTestDocument().getUser().getId())
				.accept(MediaType.APPLICATION_JSON).with(jwt())).andExpect(status().isOk()).andDo(print())
				.andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$[0].id").value(getMainTestDocument().getId()))
				.andExpect(jsonPath("$.length()").value(1));

	}

}