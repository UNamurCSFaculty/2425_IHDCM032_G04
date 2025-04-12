package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import be.labil.anacarde.domain.dto.NewsCategoryDto;
import be.labil.anacarde.domain.dto.NewsDto;
import be.labil.anacarde.domain.model.News;
import be.labil.anacarde.domain.model.NewsCategory;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NewsMapperTest {

	@Autowired
	private NewsMapper newsMapper;

	@Test
	void shouldMapToDto() {
		NewsCategory category = new NewsCategory(1, "Économie");

		News news = new News();
		news.setId(10);
		news.setTitle("Titre de test");
		news.setContent("Contenu de test");
		news.setCreationDate(LocalDateTime.now().minusDays(1));
		news.setPublicationDate(LocalDateTime.now());
		news.setCategory(category);

		NewsDto dto = newsMapper.toDto(news);

		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(news.getId());
		assertThat(dto.getTitle()).isEqualTo(news.getTitle());
		assertThat(dto.getContent()).isEqualTo(news.getContent());
		assertThat(dto.getCreationDate()).isEqualTo(news.getCreationDate());
		assertThat(dto.getPublicationDate()).isEqualTo(news.getPublicationDate());
		assertThat(dto.getCategory()).isNotNull();
		assertThat(dto.getCategory().getId()).isEqualTo(category.getId());
		assertThat(dto.getCategory().getName()).isEqualTo(category.getName());
	}

	@Test
	void shouldMapToEntity() {
		NewsCategoryDto categoryDto = new NewsCategoryDto();
		categoryDto.setId(2);
		categoryDto.setName("Culture");

		NewsDto dto = new NewsDto();
		dto.setId(20);
		dto.setTitle("Un article culturel");
		dto.setContent("Contenu culturel");
		dto.setCreationDate(LocalDateTime.now().minusHours(2));
		dto.setPublicationDate(LocalDateTime.now());
		dto.setCategory(categoryDto);

		News entity = newsMapper.toEntity(dto);

		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isEqualTo(dto.getId());
		assertThat(entity.getTitle()).isEqualTo(dto.getTitle());
		assertThat(entity.getContent()).isEqualTo(dto.getContent());
		assertThat(entity.getCreationDate()).isEqualTo(dto.getCreationDate());
		assertThat(entity.getPublicationDate()).isEqualTo(dto.getPublicationDate());
		assertThat(entity.getCategory()).isNotNull();
		assertThat(entity.getCategory().getId()).isEqualTo(categoryDto.getId());
		assertThat(entity.getCategory().getName()).isEqualTo(categoryDto.getName());
	}

	@Test
	void shouldPartialUpdateEntity() {
		NewsCategory existingCategory = new NewsCategory(5, "Sport");

		News existing = new News();
		existing.setId(1);
		existing.setTitle("Ancien titre");
		existing.setContent("Ancien contenu");
		existing.setCreationDate(LocalDateTime.now().minusDays(3));
		existing.setPublicationDate(LocalDateTime.now().minusDays(1));
		existing.setCategory(existingCategory);

		NewsDto updateDto = new NewsDto();
		updateDto.setTitle("Titre mis à jour");

		newsMapper.partialUpdate(updateDto, existing);

		assertThat(existing.getTitle()).isEqualTo("Titre mis à jour");
		assertThat(existing.getContent()).isEqualTo("Ancien contenu"); // unchanged
		assertThat(existing.getCategory()).isEqualTo(existingCategory); // unchanged
	}
}
