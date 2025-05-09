package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import be.labil.anacarde.domain.dto.db.RegionDto;
import be.labil.anacarde.domain.model.Region;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RegionMapperTest {

	@Autowired
	private RegionMapper regionMapper;

	@Test
	void shouldMapEntityToDto() {
		Region entity = new Region();
		entity.setId(1);
		entity.setName("Région Nord");

		RegionDto dto = regionMapper.toDto(entity);

		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(entity.getId());
		assertThat(dto.getName()).isEqualTo(entity.getName());
	}

	@Test
	void shouldMapDtoToEntity() {
		RegionDto dto = new RegionDto();
		dto.setId(1);
		dto.setName("Région Nord");

		Region entity = regionMapper.toEntity(dto);

		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isEqualTo(dto.getId());
		assertThat(entity.getName()).isEqualTo(dto.getName());
	}

	@Test
	void shouldPartialUpdateEntity() {
		RegionDto dto = new RegionDto();
		dto.setName("Région Sud");

		Region entity = new Region();
		entity.setId(1);
		entity.setName("Région Nord");

		regionMapper.partialUpdate(dto, entity);

		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isEqualTo(1);
		assertThat(entity.getName()).isEqualTo("Région Sud");
	}
}
