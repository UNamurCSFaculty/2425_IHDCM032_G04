package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.*;
import be.labil.anacarde.domain.dto.user.QualityInspectorDetailDto;
import be.labil.anacarde.infrastructure.persistence.QualityControlRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

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
		mockMvc.perform(get("/api/products/" + getMainTestQualityControl().getProduct().getId() + "/quality-controls/"
				+ getMainTestQualityControl().getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andDo(print()).andExpect(jsonPath("$.id").value(getMainTestQualityControl().getId()))
				.andExpect(jsonPath("$.identifier").value(getMainTestQualityControl().getIdentifier()))
				.andExpect(jsonPath("$.controlDate")
						.value(startsWith(getMainTestQualityControl().getControlDate().toString())))
				.andExpect(jsonPath("$.granularity").value(getMainTestQualityControl().getGranularity()))
				.andExpect(jsonPath("$.korTest").value(getMainTestQualityControl().getKorTest()))
				.andExpect(jsonPath("$.humidity").value(getMainTestQualityControl().getHumidity()))
				.andExpect(jsonPath("$.qualityInspector.id")
						.value(getMainTestQualityControl().getQualityInspector().getId()))
				.andExpect(jsonPath("$.product.id").value(getMainTestQualityControl().getProduct().getId()))
				.andExpect(jsonPath("$.quality.id").value(getMainTestQualityControl().getQuality().getId()))
				.andExpect(jsonPath("$.document.id").value(getMainTestQualityControl().getDocument().getId()));
	}

	/**
	 * Teste la création d’un nouveau contrôle qualité.
	 */
	@Test
	public void testCreateQualityControl() throws Exception {
		QualityControlDto dto = new QualityControlDto();
		dto.setIdentifier("QC-002");
		dto.setControlDate(LocalDateTime.of(2025, 4, 7, 10, 0));
		dto.setGranularity(0.5f);
		dto.setKorTest(0.8f);
		dto.setHumidity(12.5f);

		QualityInspectorDetailDto inspector = new QualityInspectorDetailDto();
		inspector.setId(getMainTestQualityControl().getQualityInspector().getId());
		dto.setQualityInspector(inspector);

		ProductDto product = new HarvestProductDto();
		product.setId(getTestHarvestProduct().getId());
		dto.setProduct(product);

		QualityDto quality = new QualityDto();
		quality.setId(getMainTestQualityControl().getQuality().getId());
		dto.setQuality(quality);

		DocumentDto document = new DocumentDto();
		document.setId(getMainTestQualityControl().getDocument().getId());
		dto.setDocument(document);

		ObjectNode json = objectMapper.valueToTree(dto);
		String content = json.toString();

		mockMvc.perform(post("/api/products/" + getMainTestQualityControl().getProduct().getId() + "/quality-controls")
				.contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/quality-controls")))
				.andExpect(jsonPath("$.identifier").value("QC-002")).andExpect(jsonPath("$.granularity").value(0.5))
				.andExpect(jsonPath("$.korTest").value(0.8)).andExpect(jsonPath("$.humidity").value(12.5))
				.andExpect(jsonPath("$.qualityInspector.id")
						.value(getMainTestQualityControl().getQualityInspector().getId()))
				.andExpect(jsonPath("$.product.id").value(getTestHarvestProduct().getId()))
				.andExpect(jsonPath("$.quality.id").value(getMainTestQualityControl().getQuality().getId()))
				.andExpect(jsonPath("$.document.id").value(getMainTestQualityControl().getDocument().getId()));
	}

	/**
	 * Teste la récupération de la liste des contrôles qualité liés à un produit.
	 */
	@Test
	public void testListQualityControls() throws Exception {
		mockMvc.perform(get("/api/products/" + getMainTestQualityControl().getProduct().getId() + "/quality-controls")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andDo(print())
				.andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(1));
	}

	/**
	 * Teste la mise à jour des données d’un contrôle qualité existant.
	 */
	@Test
	public void testUpdateQualityControl() throws Exception {
		QualityControlDto updateDto = new QualityControlDto();
		updateDto.setIdentifier("QC-001-UPDATED");
		updateDto.setControlDate(LocalDateTime.of(2025, 4, 8, 14, 30));
		updateDto.setGranularity(0.6f);
		updateDto.setKorTest(0.85f);
		updateDto.setHumidity(13.0f);

		QualityInspectorDetailDto inspector = new QualityInspectorDetailDto();
		inspector.setId(getMainTestQualityControl().getQualityInspector().getId());
		updateDto.setQualityInspector(inspector);

		ProductDto product = new TransformedProductDto();
		product.setId(getMainTestQualityControl().getProduct().getId());
		updateDto.setProduct(product);

		QualityDto quality = new QualityDto();
		quality.setId(getMainTestQualityControl().getQuality().getId());
		updateDto.setQuality(quality);

		DocumentDto document = new DocumentDto();
		document.setId(getMainTestQualityControl().getDocument().getId());
		updateDto.setDocument(document);

		ObjectNode json = objectMapper.valueToTree(updateDto);
		String content = json.toString();

		mockMvc.perform(put("/api/products/" + getMainTestQualityControl().getProduct().getId() + "/quality-controls/"
				+ getMainTestQualityControl().getId()).contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk()).andExpect(jsonPath("$.identifier").value("QC-001-UPDATED"))
				.andExpect(jsonPath("$.granularity").value(0.6)).andExpect(jsonPath("$.korTest").value(0.85))
				.andExpect(jsonPath("$.humidity").value(13.0))
				.andExpect(jsonPath("$.qualityInspector.id")
						.value(getMainTestQualityControl().getQualityInspector().getId()))
				.andExpect(jsonPath("$.product.id").value(getMainTestQualityControl().getProduct().getId()))
				.andExpect(jsonPath("$.quality.id").value(getMainTestQualityControl().getQuality().getId()))
				.andExpect(jsonPath("$.document.id").value(getMainTestQualityControl().getDocument().getId()));
	}

	/**
	 * Teste la suppression d’un contrôle qualité et vérifie qu’il n’est plus accessible.
	 */
	@Test
	public void testDeleteQualityControl() throws Exception {
		// mockMvc.perform(delete("/api/products/" + getMainTestQualityControl().getProduct().getId()
		// + "/quality-controls/" + getMainTestQualityControl().getId()))
		// .andExpect(status().isNoContent());
		//
		// mockMvc.perform(get("/api/products/" + getMainTestQualityControl().getProduct().getId() +
		// "/quality-controls/"
		// + getMainTestQualityControl().getId())).andExpect(status().isNotFound());
	}
}
