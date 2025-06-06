package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import be.labil.anacarde.domain.dto.db.NewsCategoryDto;
import be.labil.anacarde.domain.dto.db.NewsDto;
import be.labil.anacarde.domain.dto.write.NewsCreateDto;
import be.labil.anacarde.domain.dto.write.NewsUpdateDto;
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
		NewsCategory category = NewsCategory.builder().id(1).name("Économie").build();

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

		NewsCreateDto dto = new NewsCreateDto();
		dto.setTitle("Un article culturel");
		dto.setContent("Contenu culturel");
		dto.setPublicationDate(LocalDateTime.now());
		dto.setCategoryId(categoryDto.getId());

		News entity = newsMapper.toEntity(dto);

		assertThat(entity).isNotNull();
		assertThat(entity.getTitle()).isEqualTo(dto.getTitle());
		assertThat(entity.getContent()).isEqualTo(dto.getContent());
		assertThat(entity.getPublicationDate()).isEqualTo(dto.getPublicationDate());
		assertThat(entity.getCategory()).isNotNull();
		assertThat(entity.getCategory().getId()).isEqualTo(categoryDto.getId());
	}

	@Test
	void shouldPartialUpdateEntity() {
		NewsCategory existingCategory = NewsCategory.builder().id(5).name("Sport").build();

		News existing = new News();
		existing.setId(1);
		existing.setTitle("Ancien titre");
		existing.setContent("Ancien contenu");
		existing.setCreationDate(LocalDateTime.now().minusDays(3));
		existing.setPublicationDate(LocalDateTime.now().minusDays(1));
		existing.setCategory(existingCategory);

		NewsUpdateDto updateDto = new NewsUpdateDto();
		updateDto.setTitle("Titre mis à jour");

		newsMapper.partialUpdate(updateDto, existing);

		assertThat(existing.getTitle()).isEqualTo("Titre mis à jour");
		assertThat(existing.getContent()).isEqualTo("Ancien contenu");
		assertThat(existing.getCategory()).isEqualTo(existingCategory);
	}
}
