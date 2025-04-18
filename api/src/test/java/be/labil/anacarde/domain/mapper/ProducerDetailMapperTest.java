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
		dto.setPhone("+32444444444");

		Producer entity = mapper.toEntity(dto);

		assertThat(entity).isInstanceOf(Producer.class);
		assertThat(entity.getFirstName()).isEqualTo("Paul");
		assertThat(entity.getLastName()).isEqualTo("Farmer");
		assertThat(entity.getEmail()).isEqualTo("paul@farm.com");
		assertThat(entity.getPassword()).isEqualTo("farmsecure");
		assertThat(entity.getAgriculturalIdentifier()).isEqualTo("AGRI2025");
		assertThat(entity.getPhone()).isEqualTo("+32444444444");

		assertThat(entity.getCooperative()).isNotNull();
		assertThat(entity.getCooperative().getId()).isEqualTo(10);
		assertThat(entity.getCooperative().getName()).isEqualTo("COOP-A");

		assertThat(entity.getLanguage()).isNotNull();
		assertThat(entity.getLanguage().getId()).isEqualTo(1);

		assertThat(entity.getRoles()).isNotEmpty();
		assertThat(entity.getRoles().iterator().next().getId()).isEqualTo(30);
		assertThat(entity.getRoles().iterator().next().getName()).isEqualTo("Producer");
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
		producer.setPassword("hiddenpass");
		producer.setLanguage(lang);
		producer.setRoles(Set.of(role));
		producer.setAgriculturalIdentifier("AGRI9988");
		producer.setCooperative(coop);
		producer.setPhone("+32444444444");

		ProducerDetailDto dto = mapper.toDto(producer);

		assertThat(dto).isInstanceOf(ProducerDetailDto.class);
		assertThat(dto.getFirstName()).isEqualTo("Marie");
		assertThat(dto.getLastName()).isEqualTo("Harvest");
		assertThat(dto.getEmail()).isEqualTo("marie@farm.com");
		assertThat(dto.getPassword()).isEqualTo("hiddenpass");
		assertThat(dto.getAgriculturalIdentifier()).isEqualTo("AGRI9988");
		assertThat(dto.getPhone()).isEqualTo("+32444444444");

		assertThat(dto.getCooperative()).isNotNull();
		assertThat(dto.getCooperative().getId()).isEqualTo(10);
		assertThat(dto.getCooperative().getName()).isEqualTo("COOP-B");

		assertThat(dto.getLanguage()).isNotNull();
		assertThat(dto.getLanguage().getId()).isEqualTo(1);

		assertThat(dto.getRoles()).isNotEmpty();
		assertThat(dto.getRoles().iterator().next().getId()).isEqualTo(30);
		assertThat(dto.getRoles().iterator().next().getName()).isEqualTo("ROLE_PRODUCER");
	}

	@Test
	void shouldPartiallyUpdateProducerEntity() {
		LanguageDto languageDto = new LanguageDto();
		languageDto.setId(2);

		Language originalLanguage = new Language();
		originalLanguage.setId(1);

		Cooperative originalCoop = new Cooperative();
		originalCoop.setId(10);
		originalCoop.setName("OriginalCoop");

		Role originalRole = new Role();
		originalRole.setId(99);
		originalRole.setName("OriginalRole");

		Producer existingProducer = new Producer();
		existingProducer.setFirstName("OldProducer");
		existingProducer.setLastName("OldLastName");
		existingProducer.setEmail("old@mail.com");
		existingProducer.setPassword("oldpass");
		existingProducer.setAgriculturalIdentifier("OLD-ID");
		existingProducer.setLanguage(originalLanguage);
		existingProducer.setCooperative(originalCoop);
		existingProducer.setRoles(Set.of(originalRole));

		ProducerDetailDto dto = new ProducerDetailDto();
		dto.setFirstName("UpdatedProducer");
		dto.setEmail("updated@mail.com");
		dto.setLanguage(languageDto);

		mapper.partialUpdate(dto, existingProducer);

		assertThat(existingProducer.getFirstName()).isEqualTo("UpdatedProducer");
		assertThat(existingProducer.getEmail()).isEqualTo("updated@mail.com");
		assertThat(existingProducer.getLanguage()).isNotNull();
		assertThat(existingProducer.getLanguage().getId()).isEqualTo(2);

		assertThat(existingProducer.getLastName()).isEqualTo("OldLastName");
		assertThat(existingProducer.getPassword()).isEqualTo("oldpass");
		assertThat(existingProducer.getAgriculturalIdentifier()).isEqualTo("OLD-ID");
		assertThat(existingProducer.getCooperative()).isNotNull();
		assertThat(existingProducer.getCooperative().getId()).isEqualTo(10);
		assertThat(existingProducer.getCooperative().getName()).isEqualTo("OriginalCoop");
		assertThat(existingProducer.getRoles()).hasSize(1);
		assertThat(existingProducer.getRoles().iterator().next().getId()).isEqualTo(99);
		assertThat(existingProducer.getRoles().iterator().next().getName()).isEqualTo("OriginalRole");
	}

}
