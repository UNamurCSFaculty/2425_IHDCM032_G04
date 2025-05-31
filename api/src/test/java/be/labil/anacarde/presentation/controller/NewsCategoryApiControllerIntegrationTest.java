package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.db.NewsCategoryDto;
import be.labil.anacarde.domain.model.NewsCategory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

public class NewsCategoryApiControllerIntegrationTest extends AbstractIntegrationTest {

	@Autowired
	private ObjectMapper objectMapper;

	private final String API_URL = "/api/news-categories";

	@Test
	public void testCreateNewsCategory() throws Exception {
		NewsCategoryDto newCategoryDto = new NewsCategoryDto();
		newCategoryDto.setName("Technology");

		mockMvc.perform(post(API_URL).with(jwtAndCsrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCategoryDto)))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.name").value("Technology"))
				.andExpect(header().string("Location", containsString(API_URL + "/")));
	}

	@Test
	public void testCreateNewsCategory_DuplicateName() throws Exception {
		NewsCategoryDto newCategoryDto = new NewsCategoryDto();
		newCategoryDto.setName(getMainTestNewsCategory().getName()); // Existing name

		mockMvc.perform(post(API_URL).with(jwtAndCsrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCategoryDto)))
				.andExpect(status().isConflict());
	}

	@Test
	public void testGetNewsCategory() throws Exception {
		mockMvc.perform(get(API_URL + "/" + getMainTestNewsCategory().getId()).with(jwtAndCsrf()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(getMainTestNewsCategory().getId()))
				.andExpect(jsonPath("$.name").value(getMainTestNewsCategory().getName()));
	}

	@Test
	public void testGetNewsCategory_NotFound() throws Exception {
		mockMvc.perform(get(API_URL + "/9999").with(jwtAndCsrf())).andExpect(status().isNotFound());
	}

	@Test
	public void testListNewsCategories() throws Exception {
		mockMvc.perform(get(API_URL).with(jwtAndCsrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
				.andExpect(jsonPath("$[0].name").exists());
	}

	@Test
	public void testUpdateNewsCategory() throws Exception {
		NewsCategoryDto updateDto = new NewsCategoryDto();
		updateDto.setName("Updated Category Name");

		mockMvc.perform(put(API_URL + "/" + getMainTestNewsCategory().getId()).with(jwtAndCsrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Updated Category Name"));
	}

	@Test
	public void testUpdateNewsCategory_NameConflict() throws Exception {
		// Create another category first
		NewsCategory otherCategory = newsCategoryRepository
				.save(NewsCategory.builder().name("UniqueNameForConflictTest").build());

		NewsCategoryDto updateDto = new NewsCategoryDto();
		updateDto.setName(otherCategory.getName()); // Name that already exists for another category

		mockMvc.perform(put(API_URL + "/" + getMainTestNewsCategory().getId()) // Try to update
																				// mainTestNewsCategory
				.with(jwtAndCsrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
				.andExpect(status().isConflict());

		newsCategoryRepository.deleteById(otherCategory.getId()); // Clean up
	}

	@Test
	@Transactional
	public void testDeleteNewsCategory() throws Exception {
		NewsCategory tempCategory = NewsCategory.builder().name("Temporary Category").build();
		tempCategory = newsCategoryRepository.save(tempCategory);

		mockMvc.perform(delete(API_URL + "/" + tempCategory.getId()).with(jwtAndCsrf()))
				.andExpect(status().isNoContent());

		mockMvc.perform(get(API_URL + "/" + tempCategory.getId()).with(jwtAndCsrf()))
				.andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	public void testDeleteNewsCategory_InUse() throws Exception {
		// getMainTestNews() uses getMainTestNewsCategory()
		mockMvc.perform(
				delete(API_URL + "/" + getMainTestNewsCategory().getId()).with(jwtAndCsrf()))
				.andExpect(status().isConflict()); // Expect conflict as it's in use
	}
}
