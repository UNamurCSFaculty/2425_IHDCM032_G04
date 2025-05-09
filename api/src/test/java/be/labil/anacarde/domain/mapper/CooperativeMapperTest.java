package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import be.labil.anacarde.domain.dto.db.CooperativeDto;
import be.labil.anacarde.domain.dto.db.user.ProducerDetailDto;
import be.labil.anacarde.domain.dto.write.CooperativeUpdateDto;
import be.labil.anacarde.domain.model.Cooperative;
import be.labil.anacarde.domain.model.Producer;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CooperativeMapperTest {

	@Autowired
	private CooperativeMapper mapper;

	@Test
	void shouldMapEntityToDto() {
		Producer president = new Producer();
		president.setId(1);
		president.setFirstName("Jean");
		president.setLastName("Dupont");

		Cooperative entity = new Cooperative();
		entity.setId(100);
		entity.setName("Coopérative du Nord");
		entity.setAddress("Abobo, Abidjan");
		entity.setCreationDate(LocalDateTime.of(2023, 6, 15, 0, 0));
		entity.setPresident(president);

		CooperativeDto dto = mapper.toDto(entity);

		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(100);
		assertThat(dto.getName()).isEqualTo("Coopérative du Nord");
		assertThat(dto.getAddress()).isEqualTo("Abobo, Abidjan");
		assertThat(dto.getCreationDate()).isEqualTo("2023-06-15T00:00:00");
		assertThat(dto.getPresidentId()).isNotNull();
		assertThat(dto.getPresidentId()).isEqualTo(1);
	}

	@Test
	void shouldMapDtoToEntity() {
		ProducerDetailDto presidentDto = new ProducerDetailDto();
		presidentDto.setId(2);
		presidentDto.setFirstName("Awa");
		presidentDto.setLastName("Sanogo");

		CooperativeUpdateDto dto = new CooperativeUpdateDto();
		dto.setName("Coopérative du Sud");
		dto.setAddress("Yopougon, Abidjan");
		dto.setCreationDate(LocalDateTime.of(2024, 1, 1, 12, 0));
		dto.setPresidentId(presidentDto.getId());

		Cooperative entity = mapper.toEntity(dto);

		assertThat(entity).isNotNull();
		assertThat(entity.getName()).isEqualTo("Coopérative du Sud");
		assertThat(entity.getAddress()).isEqualTo("Yopougon, Abidjan");
		assertThat(entity.getCreationDate()).isEqualTo(LocalDateTime.of(2024, 1, 1, 12, 0));
		assertThat(entity.getPresident()).isNotNull();
		assertThat(entity.getPresident().getId()).isEqualTo(2);
	}
}
