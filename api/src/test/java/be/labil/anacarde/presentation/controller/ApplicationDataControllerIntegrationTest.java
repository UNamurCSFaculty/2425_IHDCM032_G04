package be.labil.anacarde.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class ApplicationDataControllerIntegrationTest extends AbstractIntegrationTest {

	@Test
	public void testGetApplicationData() throws Exception {
		mockMvc.perform(get("/api/app").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.languages").isArray())
				.andExpect(jsonPath("$.languages[0].code").value(getMainLanguage().getCode()))
				.andExpect(jsonPath("$.languages[0].name").value(getMainLanguage().getName()));
	}
}