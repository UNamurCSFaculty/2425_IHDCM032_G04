package be.labil.anacarde.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import be.labil.anacarde.domain.dto.db.TranslationEntryDto;
import be.labil.anacarde.domain.model.Language;
import be.labil.anacarde.domain.model.Translation;
import be.labil.anacarde.domain.model.TranslationEntry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TranslationEntryMapperTest {

	@Autowired
	private TranslationEntryMapper mapper;

	@Test
	void testToDto() {
		TranslationEntry entry = new TranslationEntry();
		entry.setId(1);
		entry.setText("Bonjour");

		Translation translation = new Translation();
		translation.setId(42);
		entry.setTranslation(translation);

		Language language = new Language();
		language.setId(2);
		entry.setLanguage(language);

		TranslationEntryDto dto = mapper.toDto(entry);

		assertNotNull(dto);
		assertEquals(1, dto.getId());
		assertEquals("Bonjour", dto.getText());
		assertEquals(42, dto.getTranslationId());
		assertEquals(2, dto.getLanguageId());
	}

	@Test
	void testToEntity() {
		TranslationEntryDto dto = new TranslationEntryDto();
		dto.setId(1);
		dto.setText("Bonjour");
		dto.setTranslationId(42);
		dto.setLanguageId(2);

		TranslationEntry entry = mapper.toEntity(dto);

		assertNotNull(entry);
		assertEquals(1, entry.getId());
		assertEquals("Bonjour", entry.getText());
		assertNotNull(entry.getTranslation());
		assertEquals(42, entry.getTranslation().getId());
		assertNotNull(entry.getLanguage());
		assertEquals(2, entry.getLanguage().getId());
	}

	@Test
	void testPartialUpdate() {
		TranslationEntry existingEntry = new TranslationEntry();
		existingEntry.setId(1);
		existingEntry.setText("Old text");

		Translation translation = new Translation();
		translation.setId(42);
		existingEntry.setTranslation(translation);

		Language language = new Language();
		language.setId(2);
		existingEntry.setLanguage(language);

		TranslationEntryDto dto = new TranslationEntryDto();
		dto.setId(1);
		dto.setText("Updated text");
		dto.setTranslationId(43);
		dto.setLanguageId(3);

		mapper.partialUpdate(dto, existingEntry);

		assertNotNull(existingEntry);
		assertEquals(1, existingEntry.getId());
		assertEquals("Updated text", existingEntry.getText());
		assertNotNull(existingEntry.getTranslation());
		assertEquals(43, existingEntry.getTranslation().getId());
		assertNotNull(existingEntry.getLanguage());
		assertEquals(3, existingEntry.getLanguage().getId());
	}
}
