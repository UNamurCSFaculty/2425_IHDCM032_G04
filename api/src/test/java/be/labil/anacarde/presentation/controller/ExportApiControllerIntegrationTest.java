package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

/**
 * Tests d’intégration du contrôleur ExportApi.
 */
@Sql(scripts = "classpath:schema-h2.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ExportApiControllerIntegrationTest extends AbstractIntegrationTest {

	/* ---------- 1. GET /api/export/auctions/{id} ---------- */
	@Test
	void getAuction_returns_one_line() throws Exception {
		var expected = getMainExportAuction();

		mockMvc.perform(get("/api/export/auctions/{id}", expected.getAuctionId())
				.with(user(getProducerTestUser())) // authentification
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.auctionId").value(expected.getAuctionId()))
				.andExpect(jsonPath("$.auctionStartPrice").value(expected.getAuctionStartPrice()))
				.andExpect(jsonPath("$.sellerId").value(expected.getSellerId()))
				.andExpect(jsonPath("$.transformedProductId")
						.value(expected.getTransformedProductId()))
				.andExpect(jsonPath("$.bidCount").value(expected.getBidCount()));
	}

	/* ---------- 2. GET /api/export/auctions/all ---------- */
	@Test
	void listAllAuctions_returns_at_least_one_row() throws Exception {
		mockMvc.perform(get("/api/export/auctions/all").with(user(getProducerTestUser()))
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(4));
	}

	/* ---------- 3. GET /api/export/auctions?start=…&end=… ---------- */
	@Test
	void listAuctions_between_two_dates() throws Exception {
		var ref = getMainExportAuction();
		LocalDateTime start = LocalDateTime.of(2025, 3, 1, 0, 0);
		LocalDateTime end = LocalDateTime.of(2025, 7, 1, 0, 0);
		String startParam = start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		String endParam = end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

		mockMvc.perform(get("/api/export/auctions").param("start", startParam)
				.param("end", endParam).param("onlyEnded", "false")
				.with(user(getProducerTestUser())).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(3));
	}
}
