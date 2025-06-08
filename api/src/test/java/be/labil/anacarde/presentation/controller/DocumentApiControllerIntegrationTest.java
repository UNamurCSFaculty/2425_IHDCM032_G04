package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jayway.jsonpath.JsonPath;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Tests d’intégration pour le contrôleur des documents.
 */
@TestPropertySource(properties = {"storage.disk.root=build/test-uploads"})
public class DocumentApiControllerIntegrationTest extends AbstractIntegrationTest {

	@AfterEach
	public void cleanUploadDir() {
		Path uploadDir = Paths.get("build/test-uploads");
		if (Files.exists(uploadDir)) {
			try (Stream<Path> paths = Files.walk(uploadDir)) {
				paths.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			} catch (IOException e) {
				System.err.println("Impossible de nettoyer build/test-uploads : " + e.getMessage());
			}
		}
	}

	/* ---------- GET one ---------- */

	@Test
	public void testGetDocument() throws Exception {
		String expectedDate = getMainTestDocument().getUploadDate()
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

		mockMvc.perform(get("/api/documents/{id}", getMainTestDocument().getId())
				.with(user(getProducerTestUser())) // authentification
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(getMainTestDocument().getId()))
				.andExpect(jsonPath("$.storagePath").value(getMainTestDocument().getStoragePath()))
				.andExpect(jsonPath("$.userId").value(getMainTestDocument().getUser().getId()))
				.andExpect(jsonPath("$.extension").value(getMainTestDocument().getExtension()))
				.andExpect(jsonPath("$.uploadDate").value(startsWith(expectedDate)))
				.andExpect(jsonPath("$.contentType").value(getMainTestDocument().getContentType()));
	}

	/* ---------- POST create ---------- */

	@Test
	public void testCreateDocumentUser() throws Exception {
		byte[] content = "Hello World".getBytes();
		MockMultipartFile filePart = new MockMultipartFile("file", // nom de la part attendue
				"attestation.pdf", "application/pdf", content);

		mockMvc.perform(multipart("/api/documents/users/{userId}", getProducerTestUser().getId())
				.file(filePart).characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON)
				.with(user(getProducerTestUser()))).andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/api/documents/")))
				.andExpect(jsonPath("$.originalFilename").value("attestation.pdf"))
				.andExpect(jsonPath("$.contentType").value("application/pdf"))
				.andExpect(jsonPath("$.extension").value("pdf"))
				.andExpect(jsonPath("$.size").value(content.length))
				.andExpect(jsonPath("$.userId").value(getProducerTestUser().getId()))
				.andExpect(jsonPath("$.storagePath").value(
						startsWith("build" + File.separator + "test-uploads" + File.separator)));
	}

	@Test
	public void testCreateDocumentQualityControl() throws Exception {
		byte[] content = "Hello World".getBytes();
		MockMultipartFile filePart = new MockMultipartFile("file", // nom de la part attendue
				"attestation.pdf", "application/pdf", content);

		mockMvc.perform(multipart("/api/documents/quality-controls/{qualityControlId}",
				getMainTestQualityControl().getId()).file(filePart).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON).with(user(getProducerTestUser())))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/api/documents/")))
				.andExpect(jsonPath("$.originalFilename").value("attestation.pdf"))
				.andExpect(jsonPath("$.contentType").value("application/pdf"))
				.andExpect(jsonPath("$.extension").value("pdf"))
				.andExpect(jsonPath("$.size").value(content.length))
				.andExpect(jsonPath("$.storagePath").value(
						startsWith("build" + File.separator + "test-uploads" + File.separator)));
	}

	/* ---------- DELETE ---------- */

	@Test
	public void testDeleteDocument() throws Exception {
		mockMvc.perform(delete("/api/documents/{id}", getMainTestDocument().getId())
				.with(user(getProducerTestUser()))).andExpect(status().isNoContent());

		mockMvc.perform(get("/api/documents/{id}", getMainTestDocument().getId())
				.with(user(getProducerTestUser()))).andExpect(status().isNotFound());
	}

	/* ---------- LIST by user ---------- */

	@Test
	public void testListDocumentsForAUser() throws Exception {
		mockMvc.perform(
				get("/api/documents/users/{userId}", getMainTestDocument().getUser().getId())
						.with(user(getProducerTestUser())).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[1].id").value(getMainTestDocument().getId()));
	}

	/** Teste le téléchargement d'un document */
	@Test
	public void testDownloadDocument() throws Exception {
		// Arrange : créer un document et enregistrer son ID
		byte[] content = "Hello World".getBytes();
		MockMultipartFile filePart = new MockMultipartFile("file", "attestation.pdf",
				"application/pdf", content);

		// Crée un document via l'API (ou insère manuellement dans la base selon ton setup)
		MvcResult result = mockMvc
				.perform(multipart("/api/documents/quality-controls/{qualityControlId}",
						getMainTestQualityControl().getId()).file(filePart)
						.characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON)
						.with(user(getProducerTestUser())))
				.andExpect(status().isCreated()).andReturn();

		String responseBody = result.getResponse().getContentAsString();
		Integer documentId = JsonPath.read(responseBody, "$.id");

		// Act + Assert : télécharger le document
		mockMvc.perform(get("/api/documents/{id}/download", documentId)
				.with(user(getProducerTestUser())).accept(MediaType.APPLICATION_OCTET_STREAM))
				.andExpect(status().isOk())
				.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
						containsString("attachment; filename=\"attestation.pdf\"")))
				.andExpect(content().contentType("application/pdf"))
				.andExpect(content().bytes(content));
	}

}
