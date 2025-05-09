package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.db.DocumentDto;
import be.labil.anacarde.infrastructure.persistence.DocumentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

/**
 * Tests d'intégration pour le contrôleur des documents.
 */
public class DocumentApiControllerIntegrationTest extends AbstractIntegrationTest {

	private @Autowired ObjectMapper objectMapper;
	private @Autowired DocumentRepository documentRepository;

	/**
	 * Teste la récupération d'un document existant via son ID.
	 */
	@Test
	public void testGetDocument() throws Exception {
		String expectedDate = getMainTestDocument().getUploadDate()
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
		mockMvc.perform(get("/api/documents/" + getMainTestDocument().getId())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
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

		mockMvc.perform(
				post("/api/documents").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/api/documents")))
				.andExpect(jsonPath("$.storagePath").value("/test/path/created.pdf"))
				.andExpect(jsonPath("$.userId").value(getProducerTestUser().getId()))
				.andExpect(jsonPath("$.uploadDate").value(startsWith(expectedDate)))
				.andExpect(jsonPath("$.format").value("PDF"))
				.andExpect(jsonPath("$.documentType").value("SOME_TYPE"));
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

		mockMvc.perform(put("/api/documents/" + getMainTestDocument().getId())
				.contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isOk())
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
		// TODO résoudre conflit
		// mockMvc.perform(delete("/api/documents/" + getMainTestDocument().getId()))
		// .andExpect(status().isNoContent());
		//
		// mockMvc.perform(get("/api/documents/" + getMainTestDocument().getId()))
		// .andExpect(status().isNotFound());
	}

	/**
	 * Teste la récupération de tous les documents associés à un utilisateur.
	 */
	@Test
	public void testListDocumentsForAUser() throws Exception {
		mockMvc.perform(get("/api/documents/users/" + getMainTestDocument().getUser().getId())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id").value(getMainTestDocument().getId()));
	}
}