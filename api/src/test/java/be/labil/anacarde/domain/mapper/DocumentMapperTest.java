package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import be.labil.anacarde.domain.dto.db.DocumentDto;
import be.labil.anacarde.domain.model.Carrier;
import be.labil.anacarde.domain.model.Document;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DocumentMapperTest {

	@Autowired
	private DocumentMapper mapper;

	@Test
	void shouldMapEntityToDto() {
		Carrier user = new Carrier();
		user.setId(42);

		Document document = new Document();
		document.setId(1);
		document.setType("PDF");
		document.setFormat("A4");
		document.setStoragePath("/documents/2025/03/test.pdf");
		document.setUploadDate(LocalDateTime.of(2025, 3, 13, 10, 15, 30));
		document.setUser(user);

		DocumentDto dto = mapper.toDto(document);

		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(1);
		assertThat(dto.getDocumentType()).isEqualTo("PDF");
		assertThat(dto.getFormat()).isEqualTo("A4");
		assertThat(dto.getStoragePath()).isEqualTo("/documents/2025/03/test.pdf");
		assertThat(dto.getUploadDate()).isEqualTo(LocalDateTime.of(2025, 3, 13, 10, 15, 30));
		assertThat(dto.getUserId()).isEqualTo(42);
	}

	@Test
	void shouldMapDtoToEntity() {
		DocumentDto dto = new DocumentDto();
		dto.setId(2);
		dto.setDocumentType("DOCX");
		dto.setFormat("A5");
		dto.setStoragePath("/documents/2025/04/test2.docx");
		dto.setUploadDate(LocalDateTime.of(2025, 4, 1, 9, 0));
		dto.setUserId(7);

		Document entity = mapper.toEntity(dto);

		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isEqualTo(2);
		assertThat(entity.getType()).isEqualTo("DOCX");
		assertThat(entity.getFormat()).isEqualTo("A5");
		assertThat(entity.getStoragePath()).isEqualTo("/documents/2025/04/test2.docx");
		assertThat(entity.getUploadDate()).isEqualTo(LocalDateTime.of(2025, 4, 1, 9, 0));
		assertThat(entity.getUser()).isNotNull();
		assertThat(entity.getUser().getId()).isEqualTo(7);
	}
}
