package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.write.QualityControlUpdateDto;
import be.labil.anacarde.domain.model.QualityControl;
import be.labil.anacarde.infrastructure.persistence.QualityControlRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

/**
 * Tests d'intégration pour le contrôleur des contrôles qualité.
 */
public class QualityControlApiControllerIntegrationTest extends AbstractIntegrationTest {
	private @Autowired ObjectMapper objectMapper;
	private @Autowired QualityControlRepository qualityControlRepository;

	/**
	 * Teste la création d’un nouveau contrôle qualité.
	 */
	@Test
	public void testGetQualityControl() throws Exception {
		mockMvc.perform(get("/api/quality-controls/" + getMainTestQualityControl().getId())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(getMainTestQualityControl().getId()))
				.andExpect(
						jsonPath("$.identifier").value(getMainTestQualityControl().getIdentifier()))
				.andExpect(jsonPath("$.controlDate")
						.value(startsWith(getMainTestQualityControl().getControlDate().toString())))
				.andExpect(jsonPath("$.granularity")
						.value(getMainTestQualityControl().getGranularity()))
				.andExpect(jsonPath("$.korTest").value(getMainTestQualityControl().getKorTest()))
				.andExpect(jsonPath("$.humidity").value(getMainTestQualityControl().getHumidity()))
				.andExpect(jsonPath("$.qualityInspector.id")
						.value(getMainTestQualityControl().getQualityInspector().getId()))
				.andExpect(jsonPath("$.quality.id")
						.value(getMainTestQualityControl().getQuality().getId()));
	}

	/**
	 * Teste la création d’un nouveau contrôle qualité.
	 */
	@Test
	public void testCreateQualityControl() throws Exception {
		QualityControlUpdateDto dto = new QualityControlUpdateDto();
		dto.setIdentifier("QC-77777");
		dto.setControlDate(LocalDateTime.of(2025, 4, 7, 10, 0));
		dto.setGranularity(0.5f);
		dto.setKorTest(0.8f);
		dto.setHumidity(12.5f);
		dto.setQualityInspectorId(getMainTestQualityControl().getQualityInspector().getId());
		dto.setQualityId(getMainTestQualityControl().getQuality().getId());

		byte[] qualityControlJson = objectMapper.writeValueAsBytes(dto);
		MockMultipartFile qualityControlPart = new MockMultipartFile("qualityControl", // nom de la
																						// part
				"qualityControl.json", MediaType.APPLICATION_JSON_VALUE, qualityControlJson);

		mockMvc.perform(multipart("/api/quality-controls").file(qualityControlPart)
				.characterEncoding("UTF-8").with(jwtAndCsrf()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/quality-controls")))
				.andExpect(jsonPath("$.identifier").value("QC-77777"))
				.andExpect(jsonPath("$.granularity").value(0.5))
				.andExpect(jsonPath("$.korTest").value(0.8))
				.andExpect(jsonPath("$.humidity").value(12.5))
				.andExpect(jsonPath("$.qualityInspector.id")
						.value(getMainTestQualityControl().getQualityInspector().getId()))
				.andExpect(jsonPath("$.quality.id")
						.value(getMainTestQualityControl().getQuality().getId()));

		QualityControl createdQualityControl = qualityControlRepository.findAll().stream()
				.filter(qc -> qc.getIdentifier().equals("QC-77777")).findFirst()
				.orElseThrow(() -> new AssertionError("QC non trouvé"));
	}

	/**
	 * Teste la création d’un nouveau contrôle qualité, avec un document attaché
	 */
	@Test
	public void testCreateQualityControlWithDocument() throws Exception {
		QualityControlUpdateDto dto = new QualityControlUpdateDto();
		dto.setIdentifier("QC-88888");
		dto.setControlDate(LocalDateTime.of(2025, 4, 7, 10, 0));
		dto.setGranularity(0.5f);
		dto.setKorTest(0.8f);
		dto.setHumidity(12.5f);
		dto.setQualityInspectorId(getMainTestQualityControl().getQualityInspector().getId());
		dto.setQualityId(getMainTestQualityControl().getQuality().getId());

		byte[] qualityControlJson = objectMapper.writeValueAsBytes(dto);
		MockMultipartFile qualityControlPart = new MockMultipartFile("qualityControl",
				"qualityControl.json", MediaType.APPLICATION_JSON_VALUE, qualityControlJson);

		byte[] pdf = "dummy pdf content".getBytes();
		MockMultipartFile documentPart = new MockMultipartFile("documents", "doc1.pdf",
				"application/pdf", pdf);

		mockMvc.perform(multipart("/api/quality-controls").file(qualityControlPart)
				.file(documentPart).characterEncoding("UTF-8").with(jwtAndCsrf())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/quality-controls")))
				.andExpect(jsonPath("$.identifier").value("QC-88888"))
				.andExpect(jsonPath("$.granularity").value(0.5))
				.andExpect(jsonPath("$.korTest").value(0.8))
				.andExpect(jsonPath("$.humidity").value(12.5))
				.andExpect(jsonPath("$.qualityInspector.id")
						.value(getMainTestQualityControl().getQualityInspector().getId()))
				.andExpect(jsonPath("$.quality.id")
						.value(getMainTestQualityControl().getQuality().getId()));

		QualityControl createdQualityControl = qualityControlRepository.findAll().stream()
				.filter(qc -> qc.getIdentifier().equals("QC-88888")).findFirst()
				.orElseThrow(() -> new AssertionError("QC non trouvé"));
	}

	/**
	 * Teste la récupération de la liste des contrôles qualité liés à un produit.
	 */
	@Test
	public void testListQualityControls() throws Exception {
		mockMvc.perform(get("/api/quality-controls").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(3));
	}
}
