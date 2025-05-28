package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import be.labil.anacarde.domain.dto.db.CooperativeDto;
import be.labil.anacarde.domain.dto.db.LanguageDto;
import be.labil.anacarde.domain.dto.db.user.ProducerDetailDto;
import be.labil.anacarde.domain.dto.write.user.create.ProducerCreateDto;
import be.labil.anacarde.domain.model.Cooperative;
import be.labil.anacarde.domain.model.Language;
import be.labil.anacarde.domain.model.Producer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProducerDetailMapperTest {

	@Autowired
	private ProducerDetailMapper mapper;

	@Test
	void shouldMapProducerDetailDtoToProducerEntity() {
		LanguageDto languageDto = LanguageDto.builder().id(1).code("fr").name("Français").build();

		CooperativeDto coopDto = new CooperativeDto();
		coopDto.setId(10);
		coopDto.setName("COOP-A");

		ProducerCreateDto dto = new ProducerCreateDto();
		dto.setFirstName("Paul");
		dto.setLastName("Farmer");
		dto.setEmail("paul@farm.com");
		dto.setPassword("farmsecure");
		dto.setAgriculturalIdentifier("AGRI2025");
		dto.setCooperativeId(coopDto.getId());
		dto.setLanguageId(languageDto.getId());
		dto.setPhone("+2290197000002");

		Producer entity = mapper.toEntity(dto);

		assertThat(entity).isInstanceOf(Producer.class);
		assertThat(entity.getFirstName()).isEqualTo("Paul");
		assertThat(entity.getLastName()).isEqualTo("Farmer");
		assertThat(entity.getEmail()).isEqualTo("paul@farm.com");
		assertThat(entity.getPassword()).isEqualTo("farmsecure");
		assertThat(entity.getAgriculturalIdentifier()).isEqualTo("AGRI2025");
		assertThat(entity.getPhone()).isEqualTo("+2290197000002");

		assertThat(entity.getCooperative()).isNotNull();
		assertThat(entity.getCooperative().getId()).isEqualTo(10);

		assertThat(entity.getLanguage()).isNotNull();
		assertThat(entity.getLanguage().getId()).isEqualTo(1);
	}

	@Test
	void shouldMapProducerEntityToProducerDetailDto() {
		Language lang = new Language();
		lang.setId(1);

		Cooperative coop = new Cooperative();
		coop.setId(10);
		coop.setName("COOP-B");

		Producer producer = new Producer();
		producer.setFirstName("Marie");
		producer.setLastName("Harvest");
		producer.setEmail("marie@farm.com");
		producer.setPassword("hiddenpass");
		producer.setLanguage(lang);
		producer.setAgriculturalIdentifier("AGRI9988");
		producer.setCooperative(coop);
		producer.setPhone("+32444444444");

		ProducerDetailDto dto = mapper.toDto(producer);

		assertThat(dto).isInstanceOf(ProducerDetailDto.class);
		assertThat(dto.getFirstName()).isEqualTo("Marie");
		assertThat(dto.getLastName()).isEqualTo("Harvest");
		assertThat(dto.getEmail()).isEqualTo("marie@farm.com");
		assertThat(dto.getAgriculturalIdentifier()).isEqualTo("AGRI9988");
		assertThat(dto.getPhone()).isEqualTo("+32444444444");

		assertThat(dto.getCooperative()).isNotNull();
		assertThat(dto.getCooperative().getId()).isEqualTo(10);
		assertThat(dto.getCooperative().getName()).isEqualTo("COOP-B");

		assertThat(dto.getLanguage()).isNotNull();
		assertThat(dto.getLanguage().getId()).isEqualTo(1);
	}
}
