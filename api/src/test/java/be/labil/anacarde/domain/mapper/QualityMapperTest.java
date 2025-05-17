package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import be.labil.anacarde.domain.dto.db.QualityDto;
import be.labil.anacarde.domain.dto.write.QualityUpdateDto;
import be.labil.anacarde.domain.model.Quality;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QualityMapperTest {

	@Autowired
	private QualityMapper qualityMapper;

	@Test
	void shouldMapEntityToDto() {
		Quality entity = new Quality();
		entity.setId(1);
		entity.setName("A");

		QualityDto dto = qualityMapper.toDto(entity);

		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(entity.getId());
		assertThat(dto.getName()).isEqualTo(entity.getName());
	}

	@Test
	void shouldMapDtoToEntity() {
		QualityUpdateDto dto = new QualityUpdateDto();
		dto.setName("A");
		dto.setQualityTypeId(1);

		Quality entity = qualityMapper.toEntity(dto);

		assertThat(entity).isNotNull();
		assertThat(entity.getQualityType().getId()).isEqualTo(dto.getQualityTypeId());
		assertThat(entity.getName()).isEqualTo(dto.getName());
	}
}
