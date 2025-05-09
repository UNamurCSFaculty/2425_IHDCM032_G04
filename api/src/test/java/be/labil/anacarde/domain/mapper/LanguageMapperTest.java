package be.labil.anacarde.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import be.labil.anacarde.domain.dto.db.LanguageDto;
import be.labil.anacarde.domain.model.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LanguageMapperTest {

	@Autowired
	private LanguageMapper languageMapper;

	@Test
	void testToDto() {
		Language language = Language.builder().id(1).name("Français").code("fr").build();

		LanguageDto dto = languageMapper.toDto(language);

		assertNotNull(dto);
		assertEquals(language.getId(), dto.getId());
		assertEquals(language.getName(), dto.getName());
	}

	@Test
	void testToEntity() {
		LanguageDto dto = LanguageDto.builder().id(2).code("en").name("Anglais").build();
		Language entity = languageMapper.toEntity(dto);

		assertNotNull(entity);
		assertEquals(dto.getId(), entity.getId());
		assertEquals(dto.getName(), entity.getName());
		assertEquals(dto.getCode(), entity.getCode());
	}
}
