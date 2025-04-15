package be.labil.anacarde.domain.mapper;

import static org.junit.jupiter.api.Assertions.*;

import be.labil.anacarde.domain.dto.NewsCategoryDto;
import be.labil.anacarde.domain.model.NewsCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NewsCategoryMapperTest {

	@Autowired
	private NewsCategoryMapper newsCategoryMapper;

	@Test
	void testToDto() {
		NewsCategory category = new NewsCategory();
		category.setId(1);
		category.setName("Sport");

		NewsCategoryDto dto = newsCategoryMapper.toDto(category);

		assertNotNull(dto);
		assertEquals(category.getId(), dto.getId());
		assertEquals(category.getName(), dto.getName());
	}

	@Test
	void testToEntity() {
		NewsCategoryDto dto = new NewsCategoryDto();
		dto.setId(2);
		dto.setName("Culture");

		NewsCategory entity = newsCategoryMapper.toEntity(dto);

		assertNotNull(entity);
		assertEquals(dto.getId(), entity.getId());
		assertEquals(dto.getName(), entity.getName());
	}

	@Test
	void testPartialUpdate() {
		NewsCategoryDto dto = new NewsCategoryDto();
		dto.setName("Économie");

		NewsCategory entity = new NewsCategory();
		entity.setId(10);
		entity.setName("Ancien Nom");

		NewsCategory updated = newsCategoryMapper.partialUpdate(dto, entity);

		assertNotNull(updated);
		assertEquals(10, updated.getId()); // ID ne change pas
		assertEquals("Économie", updated.getName()); // Name mis à jour
	}

	@Test
	void testPartialUpdate_NullDto() {
		NewsCategory entity = new NewsCategory();
		entity.setId(99);
		entity.setName("Tech");

		NewsCategory result = newsCategoryMapper.partialUpdate(null, entity);

		assertNotNull(result);
		assertEquals("Tech", result.getName());
	}
}
