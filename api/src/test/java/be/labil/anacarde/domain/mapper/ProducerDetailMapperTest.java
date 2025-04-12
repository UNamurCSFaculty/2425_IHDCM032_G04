package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import be.labil.anacarde.domain.dto.CooperativeDto;
import be.labil.anacarde.domain.dto.LanguageDto;
import be.labil.anacarde.domain.dto.RoleDto;
import be.labil.anacarde.domain.dto.user.ProducerDetailDto;
import be.labil.anacarde.domain.model.Cooperative;
import be.labil.anacarde.domain.model.Language;
import be.labil.anacarde.domain.model.Producer;
import be.labil.anacarde.domain.model.Role;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProducerDetailMapperTest {

	@Autowired
	private ProducerDetailMapper mapper;

	@Test
	void shouldMapProducerDetailDtoToProducerEntity() {
		LanguageDto languageDto = new LanguageDto();
		languageDto.setId(1);

		RoleDto roleDto = new RoleDto(30, "Producer");

		CooperativeDto coopDto = new CooperativeDto();
		coopDto.setId(10);
		coopDto.setName("COOP-A");

		ProducerDetailDto dto = new ProducerDetailDto();
		dto.setFirstName("Paul");
		dto.setLastName("Farmer");
		dto.setEmail("paul@farm.com");
		dto.setPassword("farmsecure");
		dto.setAgriculturalIdentifier("AGRI2025");
		dto.setCooperative(coopDto);
		dto.setLanguage(languageDto);
		dto.setRoles(Set.of(roleDto));

		Producer entity = mapper.toEntity(dto);

		assertThat(entity).isInstanceOf(Producer.class);
		assertThat(entity.getFirstName()).isEqualTo("Paul");
		assertThat(entity.getAgriculturalIdentifier()).isEqualTo("AGRI2025");
		assertThat(entity.getCooperative()).isNotNull();
		assertThat(entity.getRoles()).isNotEmpty();
		assertThat(entity.getLanguage()).isNotNull();
	}

	@Test
	void shouldMapProducerEntityToProducerDetailDto() {
		Language lang = new Language();
		lang.setId(1);

		Role role = new Role();
		role.setId(30);
		role.setName("ROLE_PRODUCER");

		Cooperative coop = new Cooperative();
		coop.setId(10);
		coop.setName("COOP-B");

		Producer producer = new Producer();
		producer.setFirstName("Marie");
		producer.setLastName("Harvest");
		producer.setEmail("marie@farm.com");
		producer.setLanguage(lang);
		producer.setRoles(Set.of(role));
		producer.setAgriculturalIdentifier("AGRI9988");
		producer.setCooperative(coop);

		ProducerDetailDto dto = mapper.toDto(producer);

		assertThat(dto).isInstanceOf(ProducerDetailDto.class);
		assertThat(dto.getEmail()).isEqualTo("marie@farm.com");
		assertThat(dto.getAgriculturalIdentifier()).isEqualTo("AGRI9988");
		assertThat(dto.getCooperative()).isNotNull();
		assertThat(dto.getLanguage()).isNotNull();
		assertThat(dto.getRoles()).isNotEmpty();
	}

	@Test
	void shouldPartiallyUpdateProducerEntity() {
		LanguageDto languageDto = new LanguageDto();
		languageDto.setId(2);

		Producer existingProducer = new Producer();
		existingProducer.setFirstName("OldProducer");
		existingProducer.setLanguage(new Language());
		existingProducer.setRoles(new HashSet<>());

		ProducerDetailDto dto = new ProducerDetailDto();
		dto.setFirstName("UpdatedProducer");
		dto.setLanguage(languageDto);

		mapper.partialUpdate(dto, existingProducer);

		assertThat(existingProducer.getFirstName()).isEqualTo("UpdatedProducer");
		assertThat(existingProducer.getLanguage()).isNotNull();
		assertThat(existingProducer.getRoles()).isEmpty();
	}
}
