package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import be.labil.anacarde.domain.dto.LanguageDto;
import be.labil.anacarde.domain.dto.RoleDto;
import be.labil.anacarde.domain.dto.user.CarrierDetailDto;
import be.labil.anacarde.domain.model.Carrier;
import be.labil.anacarde.domain.model.Language;
import be.labil.anacarde.domain.model.Region;
import be.labil.anacarde.domain.model.Role;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CarrierDetailMapperTest {

	@Autowired
	private CarrierDetailMapper mapper;

	@Test
	void shouldMapCarrierDetailDtoToCarrierEntity() {
		LanguageDto languageDto = new LanguageDto();
		languageDto.setId(1);

		RoleDto roleDto = new RoleDto(10, "CARRIER_ROLE");

		CarrierDetailDto dto = new CarrierDetailDto();
		dto.setFirstName("Camille");
		dto.setLastName("Transport");
		dto.setEmail("camille@transporteur.com");
		dto.setPassword("secretpass");
		dto.setLanguage(languageDto);
		dto.setRoles(Set.of(roleDto));
		dto.setPricePerKm(BigDecimal.valueOf(1.75));
		dto.setRegionIds(Set.of(100, 101));
		dto.setPhone("+32789098757");
		dto.setEnabled(true);
		dto.setRegistrationDate(LocalDateTime.of(2024, 4, 12, 15, 30));
		dto.setValidationDate(LocalDateTime.of(2024, 4, 13, 10, 0));

		Carrier entity = mapper.toEntity(dto);

		assertThat(entity).isInstanceOf(Carrier.class);

		assertThat(entity.getFirstName()).isEqualTo("Camille");
		assertThat(entity.getLastName()).isEqualTo("Transport");
		assertThat(entity.getEmail()).isEqualTo("camille@transporteur.com");
		assertThat(entity.getPassword()).isEqualTo("secretpass");
		assertThat(entity.getLanguage()).isNotNull();
		assertThat(entity.getLanguage().getId()).isEqualTo(1);
		assertThat(entity.getRoles()).hasSize(1);
		assertThat(entity.getRoles()).extracting(Role::getId).containsExactly(10);
		assertThat(entity.getPricePerKm()).isEqualByComparingTo("1.75");
		assertThat(entity.getRegions()).hasSize(2);
		assertThat(entity.getRegions()).extracting(Region::getId).containsExactlyInAnyOrder(100, 101);
		assertThat(entity.getPhone()).isEqualTo("+32789098757");
		assertThat(entity.isEnabled()).isTrue();
		assertThat(entity.getRegistrationDate()).isEqualTo(LocalDateTime.of(2024, 4, 12, 15, 30));
		assertThat(entity.getValidationDate()).isEqualTo(LocalDateTime.of(2024, 4, 13, 10, 0));
		assertThat(entity.getRegions()).extracting(Region::getId).containsExactlyInAnyOrder(100, 101);
	}

	@Test
	void shouldMapCarrierEntityToCarrierDetailDto() {
		Language lang = new Language();
		lang.setId(2);

		Role role = new Role();
		role.setId(20);
		role.setName("CARRIER_ROLE");

		Region r1 = new Region();
		r1.setId(200);
		Region r2 = new Region();
		r2.setId(201);

		Carrier carrier = new Carrier();
		carrier.setFirstName("Logan");
		carrier.setLastName("Driver");
		carrier.setEmail("logan@carrier.org");
		carrier.setPassword("secret123");
		carrier.setLanguage(lang);
		carrier.setRoles(Set.of(role));
		carrier.setPricePerKm(BigDecimal.valueOf(2.30));
		carrier.setRegions(Set.of(r1, r2));
		carrier.setPhone("+32123456789");
		carrier.setEnabled(true);
		carrier.setRegistrationDate(LocalDateTime.of(2025, 1, 1, 8, 0));
		carrier.setValidationDate(LocalDateTime.of(2025, 1, 2, 9, 0));

		CarrierDetailDto dto = mapper.toDto(carrier);

		assertThat(dto).isInstanceOf(CarrierDetailDto.class);
		assertThat(dto.getFirstName()).isEqualTo("Logan");
		assertThat(dto.getLastName()).isEqualTo("Driver");
		assertThat(dto.getEmail()).isEqualTo("logan@carrier.org");
		assertThat(dto.getPassword()).isEqualTo("secret123");
		assertThat(dto.getLanguage()).isNotNull();
		assertThat(dto.getLanguage().getId()).isEqualTo(2);
		assertThat(dto.getRoles()).hasSize(1);
		assertThat(dto.getRoles()).extracting(RoleDto::getId).containsExactly(20);
		assertThat(dto.getPricePerKm()).isEqualByComparingTo("2.30");
		assertThat(dto.getRegionIds()).containsExactlyInAnyOrder(200, 201);
		assertThat(dto.getPhone()).isEqualTo("+32123456789");
		assertThat(dto.isEnabled()).isTrue();
		assertThat(dto.getRegistrationDate()).isEqualTo(LocalDateTime.of(2025, 1, 1, 8, 0));
		assertThat(dto.getValidationDate()).isEqualTo(LocalDateTime.of(2025, 1, 2, 9, 0));
	}

	@Test
	void shouldMapRegionsToIds() {
		Region region1 = new Region();
		region1.setId(100);
		Region region2 = new Region();
		region2.setId(200);

		Set<Region> regions = Set.of(region1, region2);

		Set<Integer> regionIds = mapper.mapRegionsToIds(regions);

		assertThat(regionIds).containsExactlyInAnyOrder(100, 200);
	}

	@Test
	void shouldMapIdsToRegions() {
		Set<Integer> ids = Set.of(100, 200);

		Set<Region> regions = mapper.mapIdsToRegions(ids);

		assertThat(regions).hasSize(2);
		assertThat(regions).extracting(Region::getId).containsExactlyInAnyOrder(100, 200);
	}
}
