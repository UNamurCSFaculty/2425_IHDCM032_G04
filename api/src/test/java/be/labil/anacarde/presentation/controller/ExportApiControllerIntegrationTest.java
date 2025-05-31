package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

/**
 * Tests d’intégration du contrôleur AdminExportApi.
 */
@Sql(scripts = "classpath:schema-h2.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ExportApiControllerIntegrationTest extends AbstractIntegrationTest {

	private static final String EXPORT_URL = "/api/admin/export/auctions";
	private static final String CSV_MIME_TYPE = "text/csv";

	/*
	 * ---------- GET /api/admin/export/auctions (toutes les données, format JSON par défaut)
	 * ----------
	 */
	@Test
	void exportAuctions_defaultParams_returnsAllDataAsJson() throws Exception {
		mockMvc.perform(get(EXPORT_URL).with(user(getProducerTestUser()))
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
						containsString("attachment; filename=\"auctions_export_")))
				.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, endsWith(".json\"")))
				.andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(4)); // Suppose
																								// 4
																								// éléments
																								// au
																								// total
	}

	/*
	 * ---------- GET /api/admin/export/auctions?start=…&end=…&onlyEnded=false (données filtrées,
	 * format JSON par défaut) ----------
	 */
	@Test
	void exportAuctions_withDateRangeAndOnlyEndedFalse_returnsFilteredDataAsJson()
			throws Exception {
		LocalDateTime start = LocalDateTime.of(2025, 3, 1, 0, 0);
		LocalDateTime end = LocalDateTime.of(2025, 7, 1, 0, 0);
		String startParam = start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		String endParam = end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

		mockMvc.perform(get(EXPORT_URL).param("start", startParam).param("end", endParam)
				.param("onlyEnded", "false").with(user(getProducerTestUser()))
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
						containsString("attachment; filename=\"auctions_export_")))
				.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, endsWith(".json\"")))
				.andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(3)); // Suppose
																								// 3
																								// éléments
																								// dans
																								// cette
																								// plage
	}

	/*
	 * ---------- GET /api/admin/export/auctions?start=…&end=…&onlyEnded=true (données filtrées,
	 * format JSON) ----------
	 */
	@Test
	void exportAuctions_withDateRangeAndOnlyEndedTrue_returnsFilteredDataAsJson() throws Exception {
		LocalDateTime start = LocalDateTime.of(2025, 3, 1, 0, 0);
		LocalDateTime end = LocalDateTime.of(2025, 7, 1, 0, 0);
		String startParam = start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		String endParam = end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

		mockMvc.perform(get(EXPORT_URL).param("start", startParam).param("end", endParam)
				.param("onlyEnded", "true").with(user(getProducerTestUser()))
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
						containsString("attachment; filename=\"auctions_export_")))
				.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, endsWith(".json\"")))
				.andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(2));
	}

	/*
	 * ---------- GET /api/admin/export/auctions?onlyEnded=true (données filtrées, format JSON)
	 * ----------
	 */
	@Test
	void exportAuctions_onlyEndedTrue_returnsFilteredDataAsJson() throws Exception {
		mockMvc.perform(get(EXPORT_URL).param("onlyEnded", "true").with(user(getProducerTestUser()))
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
						containsString("attachment; filename=\"auctions_export_")))
				.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, endsWith(".json\"")))
				.andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(4));
	}

	/*
	 * ---------- GET /api/admin/export/auctions?format=csv (toutes les données, format CSV)
	 * ----------
	 */
	@Test
	void exportAuctions_defaultParams_returnsAllDataAsCsv() throws Exception {
		mockMvc.perform(get(EXPORT_URL).param("format", "csv").with(user(getProducerTestUser()))
				.accept(CSV_MIME_TYPE)).andExpect(status().isOk())
				.andExpect(content().contentType(CSV_MIME_TYPE))
				.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
						containsString("attachment; filename=\"auctions_export_")))
				.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, endsWith(".csv\"")))
				.andExpect(content().string(not(emptyOrNullString())));
	}

	/*
	 * ---------- GET /api/admin/export/auctions?start=…&end=…&format=csv (données filtrées, format
	 * CSV) ----------
	 */
	@Test
	void exportAuctions_withDateRangeAndFormatCsv_returnsFilteredDataAsCsv() throws Exception {
		LocalDateTime start = LocalDateTime.of(2025, 3, 1, 0, 0);
		LocalDateTime end = LocalDateTime.of(2025, 7, 1, 0, 0);
		String startParam = start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		String endParam = end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

		mockMvc.perform(get(EXPORT_URL).param("start", startParam).param("end", endParam)
				.param("format", "csv").with(user(getProducerTestUser())).accept(CSV_MIME_TYPE))
				.andExpect(status().isOk()).andExpect(content().contentType(CSV_MIME_TYPE))
				.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
						containsString("attachment; filename=\"auctions_export_")))
				.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, endsWith(".csv\"")))
				.andExpect(content().string(not(emptyOrNullString())));
	}

	/* ---------- Tests des cas d'erreur pour les dates ---------- */
	@Test
	void exportAuctions_invalidDateRange_startAfterEnd_returnsBadRequest() throws Exception {
		LocalDateTime start = LocalDateTime.of(2025, 7, 1, 0, 0);
		LocalDateTime end = LocalDateTime.of(2025, 3, 1, 0, 0);
		String startParam = start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		String endParam = end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

		mockMvc.perform(get(EXPORT_URL).param("start", startParam).param("end", endParam)
				.with(user(getProducerTestUser())).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	void exportAuctions_invalidDateRange_onlyStartProvided_returnsBadRequest() throws Exception {
		LocalDateTime start = LocalDateTime.of(2025, 3, 1, 0, 0);
		String startParam = start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

		mockMvc.perform(get(EXPORT_URL).param("start", startParam).with(user(getProducerTestUser()))
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@Test
	void exportAuctions_invalidDateRange_onlyEndProvided_returnsBadRequest() throws Exception {
		LocalDateTime end = LocalDateTime.of(2025, 7, 1, 0, 0);
		String endParam = end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

		mockMvc.perform(get(EXPORT_URL).param("end", endParam).with(user(getProducerTestUser()))
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@Test
	void exportAuctions_invalidFormatParam_defaultsToJson() throws Exception {
		mockMvc.perform(get(EXPORT_URL).param("format", "invalid_format")
				.with(user(getProducerTestUser())).accept(MediaType.ALL)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, endsWith(".json\"")))
				.andExpect(jsonPath("$.length()").value(4));
	}
}