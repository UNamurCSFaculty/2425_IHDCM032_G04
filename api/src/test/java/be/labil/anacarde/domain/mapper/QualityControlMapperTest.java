package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import be.labil.anacarde.domain.dto.db.QualityControlDto;
import be.labil.anacarde.domain.dto.write.DocumentUpdateDto;
import be.labil.anacarde.domain.dto.write.QualityControlUpdateDto;
import be.labil.anacarde.domain.model.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QualityControlMapperTest {

	@Autowired
	private QualityControlMapper qualityControlMapper;

	@Test
	void shouldMapEntityToDto() {
		QualityInspector qualityInspector = new QualityInspector();
		qualityInspector.setId(1);

		Product product = new HarvestProduct();
		product.setId(1);

		Quality quality = new Quality();
		quality.setId(1);

		Document document = new Document();
		document.setId(1);

		QualityControl entity = new QualityControl();
		entity.setId(1);
		entity.setIdentifier("QC-001");
		entity.setControlDate(LocalDateTime.of(2025, 4, 7, 10, 0));
		entity.setGranularity(0.5f);
		entity.setKorTest(0.8f);
		entity.setHumidity(12.5f);
		entity.setQualityInspector(qualityInspector);
		entity.setProduct(product);
		entity.setQuality(quality);
		entity.setDocument(document);

		QualityControlDto dto = qualityControlMapper.toDto(entity);

		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(entity.getId());
		assertThat(dto.getIdentifier()).isEqualTo(entity.getIdentifier());
		assertThat(dto.getControlDate()).isEqualTo(entity.getControlDate());
		assertThat(dto.getGranularity()).isEqualTo(entity.getGranularity());
		assertThat(dto.getKorTest()).isEqualTo(entity.getKorTest());
		assertThat(dto.getHumidity()).isEqualTo(entity.getHumidity());
		assertThat(dto.getQualityInspector()).isNotNull();
		assertThat(dto.getProduct()).isNotNull();
		assertThat(dto.getQuality()).isNotNull();
		assertThat(dto.getDocument()).isNotNull();
	}

	@Test
	void shouldMapDtoToEntity() {
		QualityControlUpdateDto dto = new QualityControlUpdateDto();
		dto.setIdentifier("QC-001");
		dto.setControlDate(LocalDateTime.of(2025, 4, 7, 10, 0));
		dto.setGranularity(0.5f);
		dto.setKorTest(0.8f);
		dto.setHumidity(12.5f);
		dto.setQualityInspectorId(1);
		dto.setProductId(2);
		dto.setQualityId(3);
		dto.setDocument(new DocumentUpdateDto());

		QualityControl entity = qualityControlMapper.toEntity(dto);

		assertThat(entity).isNotNull();
		assertThat(entity.getIdentifier()).isEqualTo(dto.getIdentifier());
		assertThat(entity.getControlDate()).isEqualTo(dto.getControlDate());
		assertThat(entity.getGranularity()).isEqualTo(dto.getGranularity());
		assertThat(entity.getKorTest()).isEqualTo(dto.getKorTest());
		assertThat(entity.getHumidity()).isEqualTo(dto.getHumidity());
		assertThat(entity.getQualityInspector().getId()).isEqualTo(1);
		assertThat(entity.getProduct().getId()).isEqualTo(2);
		assertThat(entity.getQuality().getId()).isEqualTo(3);
		assertThat(entity.getDocument()).isNotNull();
	}

	@Test
	void shouldPartialUpdateEntity() {
		QualityControlUpdateDto dto = new QualityControlUpdateDto();
		dto.setIdentifier("QC-002"); // Change identifier only
		dto.setControlDate(LocalDateTime.of(2025, 5, 1, 11, 0));

		QualityControl existingEntity = new QualityControl();
		existingEntity.setId(1);
		existingEntity.setIdentifier("QC-001");
		existingEntity.setControlDate(LocalDateTime.of(2025, 4, 7, 10, 0));

		QualityControl updatedEntity = qualityControlMapper.partialUpdate(dto, existingEntity);

		assertThat(updatedEntity).isNotNull();
		assertThat(updatedEntity.getId()).isEqualTo(existingEntity.getId());
		assertThat(updatedEntity.getIdentifier()).isEqualTo(dto.getIdentifier());
		assertThat(updatedEntity.getControlDate()).isEqualTo(dto.getControlDate());
	}
}
