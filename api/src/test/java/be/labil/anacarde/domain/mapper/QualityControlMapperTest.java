package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import be.labil.anacarde.domain.dto.DocumentDto;
import be.labil.anacarde.domain.dto.HarvestProductDto;
import be.labil.anacarde.domain.dto.QualityControlDto;
import be.labil.anacarde.domain.dto.QualityDto;
import be.labil.anacarde.domain.dto.user.QualityInspectorDetailDto;
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
		QualityControlDto dto = new QualityControlDto();
		dto.setId(1);
		dto.setIdentifier("QC-001");
		dto.setControlDate(LocalDateTime.of(2025, 4, 7, 10, 0));
		dto.setGranularity(0.5f);
		dto.setKorTest(0.8f);
		dto.setHumidity(12.5f);
		dto.setQualityInspector(new QualityInspectorDetailDto());
		dto.setProduct(new HarvestProductDto());
		dto.setQuality(new QualityDto());
		dto.setDocument(new DocumentDto());

		QualityControl entity = qualityControlMapper.toEntity(dto);

		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isEqualTo(dto.getId());
		assertThat(entity.getIdentifier()).isEqualTo(dto.getIdentifier());
		assertThat(entity.getControlDate()).isEqualTo(dto.getControlDate());
		assertThat(entity.getGranularity()).isEqualTo(dto.getGranularity());
		assertThat(entity.getKorTest()).isEqualTo(dto.getKorTest());
		assertThat(entity.getHumidity()).isEqualTo(dto.getHumidity());
		assertThat(entity.getQualityInspector()).isNotNull();
		assertThat(entity.getProduct()).isNotNull();
		assertThat(entity.getQuality()).isNotNull();
		assertThat(entity.getDocument()).isNotNull();
	}

	@Test
	void shouldPartialUpdateEntity() {
		QualityControlDto dto = new QualityControlDto();
		dto.setId(1);
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
