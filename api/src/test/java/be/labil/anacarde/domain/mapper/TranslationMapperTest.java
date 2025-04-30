package be.labil.anacarde.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import be.labil.anacarde.domain.dto.TranslationDto;
import be.labil.anacarde.domain.model.Translation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TranslationMapperTest {

	@Autowired
	private TranslationMapper mapper;

	@Test
	void testToDto() {
		Translation translation = new Translation();
		translation.setId(1);
		translation.setKey("home.title");

		TranslationDto dto = mapper.toDto(translation);

		assertNotNull(dto);
		assertEquals(translation.getId(), dto.getId());
		assertEquals(translation.getKey(), dto.getKey());
	}

	@Test
	void testToEntity() {
		TranslationDto dto = new TranslationDto();
		dto.setId(1);
		dto.setKey("home.title");

		Translation translation = mapper.toEntity(dto);

		assertNotNull(translation);
		assertEquals(dto.getId(), translation.getId());
		assertEquals(dto.getKey(), translation.getKey());
	}

	@Test
	void testPartialUpdate() {
		Translation existingTranslation = new Translation();
		existingTranslation.setId(1);
		existingTranslation.setKey("old.key");

		TranslationDto dto = new TranslationDto();
		dto.setId(1);
		dto.setKey("home.title");

		mapper.partialUpdate(dto, existingTranslation);

		assertNotNull(existingTranslation);
		assertEquals(1, existingTranslation.getId());
		assertEquals("home.title", existingTranslation.getKey());
	}
}
