package be.labil.anacarde.presentation.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.labil.anacarde.domain.dto.write.NewsCreateDto;
import be.labil.anacarde.domain.dto.write.NewsUpdateDto;
import be.labil.anacarde.domain.model.News;
import be.labil.anacarde.domain.model.NewsCategory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

public class NewsApiControllerIntegrationTest extends AbstractIntegrationTest {

	@Autowired
	private ObjectMapper objectMapper;

	private final String API_URL = "/api/news";

	@Test
	public void testCreateNews() throws Exception {
		NewsCreateDto newNewsDto = new NewsCreateDto();
		newNewsDto.setTitle("New Exciting Article With Author Name");
		newNewsDto.setContent("This is the content of the new exciting article with author name.");
		newNewsDto.setPublicationDate(
				LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS));
		newNewsDto.setAuthorName("Specific Test Author");
		newNewsDto.setCategoryId(getMainTestNewsCategory().getId());

		mockMvc.perform(post(API_URL).with(jwtAndCsrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newNewsDto)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.title").value(newNewsDto.getTitle()))
				.andExpect(jsonPath("$.content").value(newNewsDto.getContent()))
				.andExpect(jsonPath("$.publicationDate").value(objectMapper
						.writeValueAsString(newNewsDto.getPublicationDate()).replace("\"", "")))
				.andExpect(jsonPath("$.category.id").value(getMainTestNewsCategory().getId()))
				.andExpect(jsonPath("$.category.name").value(getMainTestNewsCategory().getName()))
				.andExpect(jsonPath("$.authorName").value(newNewsDto.getAuthorName()))
				.andExpect(header().string("Location", containsString(API_URL + "/")));
	}

	@Test
	public void testCreateNews_defaultAuthorAndPublicationDate() throws Exception {
		NewsCreateDto newNewsDto = new NewsCreateDto();
		newNewsDto.setTitle("New Article Default Author");
		newNewsDto.setContent("Content here.");
		newNewsDto.setCategoryId(getMainTestNewsCategory().getId());

		mockMvc.perform(post(API_URL).with(jwtAndCsrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newNewsDto)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.title").value(newNewsDto.getTitle()))
				.andExpect(jsonPath("$.content").value(newNewsDto.getContent()))
				.andExpect(jsonPath("$.publicationDate").exists())
				.andExpect(jsonPath("$.category.id").value(getMainTestNewsCategory().getId()))
				.andExpect(jsonPath("$.authorName").value(
						getMainTestUser().getFirstName() + " " + getMainTestUser().getLastName()))
				.andExpect(header().string("Location", containsString(API_URL + "/")));
	}

	@Test
	public void testGetNews() throws Exception {
		News mainTestNews = getMainTestNews();
		mockMvc.perform(get(API_URL + "/" + mainTestNews.getId()).with(jwtAndCsrf()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(mainTestNews.getId()))
				.andExpect(jsonPath("$.title").value(mainTestNews.getTitle()))
				.andExpect(jsonPath("$.content").value(mainTestNews.getContent()))
				.andExpect(jsonPath("$.publicationDate").exists())
				.andExpect(jsonPath("$.category.id").value(mainTestNews.getCategory().getId()))
				.andExpect(jsonPath("$.category.name").value(mainTestNews.getCategory().getName()))
				.andExpect(jsonPath("$.authorName").value(mainTestNews.getAuthorName()));
	}

	@Test
	public void testGetNews_NotFound() throws Exception {
		mockMvc.perform(get(API_URL + "/99999").with(jwtAndCsrf()))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testListNews() throws Exception {
		mockMvc.perform(get(API_URL).with(jwtAndCsrf()).param("size", "5").param("page", "0")
				.param("sort", "publicationDate,desc")).andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
				.andExpect(jsonPath("$.content[*].id", hasItem(getMainTestNews().getId())))
				.andExpect(jsonPath("$.content[*].title", hasItem(getMainTestNews().getTitle())))
				.andExpect(jsonPath("$.totalPages").isNumber())
				.andExpect(jsonPath("$.totalElements").isNumber())
				.andExpect(jsonPath("$.size").value(5)).andExpect(jsonPath("$.number").value(0));
	}

	@Test
	@Transactional
	public void testListNews_ByCategoryAndAuthor() throws Exception {
		NewsCreateDto specificNewsUpdateDto = new NewsCreateDto();
		specificNewsUpdateDto.setTitle("Specific Test Article by MainUser for Filtering");
		specificNewsUpdateDto.setContent("Content for specific filtering test.");
		specificNewsUpdateDto.setPublicationDate(
				LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS));
		specificNewsUpdateDto.setAuthorName(
				getMainTestUser().getFirstName() + " " + getMainTestUser().getLastName());
		specificNewsUpdateDto.setCategoryId(getMainTestNewsCategory().getId());

		String response = mockMvc
				.perform(post(API_URL).with(jwtAndCsrf()).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(specificNewsUpdateDto)))
				.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
		News createdNews = objectMapper.readValue(response, News.class);

		mockMvc.perform(get(API_URL).with(jwtAndCsrf())
				.param("authorName",
						getMainTestUser().getFirstName() + " " + getMainTestUser().getLastName())
				.param("categoryId", String.valueOf(getMainTestNewsCategory().getId()))
				.param("size", "5").param("sort", "publicationDate,desc"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
				.andExpect(jsonPath("$.content[0].id").value(createdNews.getId()))
				.andExpect(jsonPath("$.content[0].title").value(specificNewsUpdateDto.getTitle()))
				.andExpect(jsonPath("$.content[0].authorName")
						.value(specificNewsUpdateDto.getAuthorName()))
				.andExpect(jsonPath("$.content[0].category.id")
						.value(specificNewsUpdateDto.getCategoryId()));
	}

	@Test
	@Transactional
	public void testListNews_ByCategory() throws Exception {
		NewsCategory otherCategory = newsCategoryRepository.save(NewsCategory.builder()
				.name("Tech Category For List Test").description("Tech news").build());
		News otherNews = newsRepository.save(
				News.builder().title("Tech News For List Test").content("Content for tech news.")
						.publicationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
						.category(otherCategory).authorName("Tech Author").build());

		mockMvc.perform(get(API_URL).with(jwtAndCsrf()).param("categoryId",
				String.valueOf(otherCategory.getId()))).andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(1)))
				.andExpect(jsonPath("$.content[0].id").value(otherNews.getId()))
				.andExpect(jsonPath("$.content[0].title").value(otherNews.getTitle()))
				.andExpect(jsonPath("$.content[0].category.id").value(otherCategory.getId()));
	}

	@Test
	@Transactional
	public void testUpdateNews_allFields() throws Exception {
		News newsToUpdate = getMainTestNews();

		NewsUpdateDto updateDto = new NewsUpdateDto();
		updateDto.setTitle("Updated Title Completely");
		updateDto.setContent("Updated content completely.");
		updateDto.setPublicationDate(
				LocalDateTime.now().plusDays(5).truncatedTo(ChronoUnit.SECONDS));
		updateDto.setAuthorName("Completely New Author");

		NewsCategory anotherCategory = newsCategoryRepository
				.save(NewsCategory.builder().name("Another Category For Update").build());
		updateDto.setCategoryId(anotherCategory.getId());

		mockMvc.perform(put(API_URL + "/" + newsToUpdate.getId()).with(jwtAndCsrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(newsToUpdate.getId()))
				.andExpect(jsonPath("$.title").value(updateDto.getTitle()))
				.andExpect(jsonPath("$.content").value(updateDto.getContent()))
				.andExpect(jsonPath("$.publicationDate").value(objectMapper
						.writeValueAsString(updateDto.getPublicationDate()).replace("\"", "")))
				.andExpect(jsonPath("$.category.id").value(updateDto.getCategoryId()))
				.andExpect(jsonPath("$.category.name").value(anotherCategory.getName()))
				.andExpect(jsonPath("$.authorName").value(newsToUpdate.getAuthorName()));
	}

	@Test
	@Transactional
	public void testUpdateNews_partialFields_titleAndCategory() throws Exception {
		News newsToUpdate = getMainTestNews();
		String originalContent = newsToUpdate.getContent();
		String originalAuthor = newsToUpdate.getAuthorName();
		LocalDateTime originalPublicationDate = newsToUpdate.getPublicationDate();

		NewsUpdateDto updateDto = new NewsUpdateDto();
		updateDto.setTitle("Partially Updated Title");

		NewsCategory newCategory = newsCategoryRepository
				.save(NewsCategory.builder().name("Special Category For Partial Update").build());
		updateDto.setCategoryId(newCategory.getId());

		mockMvc.perform(put(API_URL + "/" + newsToUpdate.getId()).with(jwtAndCsrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(newsToUpdate.getId()))
				.andExpect(jsonPath("$.title").value(updateDto.getTitle()))
				.andExpect(jsonPath("$.content").value(originalContent))
				.andExpect(jsonPath("$.publicationDate").value(
						objectMapper.writeValueAsString(originalPublicationDate).replace("\"", "")))

				.andExpect(jsonPath("$.category.id").value(newCategory.getId()))
				.andExpect(jsonPath("$.category.name").value(newCategory.getName()))
				.andExpect(jsonPath("$.authorName").value(originalAuthor));
	}

	@Test
	@Transactional
	public void testDeleteNews() throws Exception {
		News tempNews = News.builder().title("To Be Deleted").content("Delete me.")
				.publicationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
				.category(getMainTestNewsCategory()).authorName("Temporary Author").build();
		tempNews = newsRepository.save(tempNews);

		mockMvc.perform(delete(API_URL + "/" + tempNews.getId()).with(jwtAndCsrf()))
				.andExpect(status().isNoContent());

		mockMvc.perform(get(API_URL + "/" + tempNews.getId()).with(jwtAndCsrf()))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testCreateNews_validationError_missingTitle() throws Exception {
		NewsCreateDto newNewsDto = new NewsCreateDto();
		newNewsDto.setContent("Content without title.");
		newNewsDto.setPublicationDate(LocalDateTime.now().plusDays(1));
		newNewsDto.setCategoryId(getMainTestNewsCategory().getId());

		mockMvc.perform(post(API_URL).with(jwtAndCsrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newNewsDto)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[?(@.field == 'title')]").exists());
	}

	@Test
	public void testCreateNews_validationError_missingCategory() throws Exception {
		NewsCreateDto newNewsDto = new NewsCreateDto();
		newNewsDto.setTitle("Article without category");
		newNewsDto.setContent("Content here.");
		newNewsDto.setPublicationDate(LocalDateTime.now().plusDays(1));

		mockMvc.perform(post(API_URL).with(jwtAndCsrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newNewsDto)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[?(@.field == 'categoryId')]").exists());
	}
}