package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import be.labil.anacarde.domain.dto.LanguageDto;
import be.labil.anacarde.domain.dto.RoleDto;
import be.labil.anacarde.domain.dto.user.AdminDetailDto;
import be.labil.anacarde.domain.model.Admin;
import be.labil.anacarde.domain.model.Language;
import be.labil.anacarde.domain.model.Role;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AdminDetailMapperTest {

	@Autowired
	private AdminDetailMapper mapper;
	@Test
	void shouldMapAdminDetailDtoToAdminEntity() {
		LanguageDto languageDto = new LanguageDto();
		languageDto.setId(1);

		RoleDto roleDto = new RoleDto(10, "Admin");

		AdminDetailDto dto = new AdminDetailDto();
		dto.setFirstName("Alice");
		dto.setLastName("Doe");
		dto.setEmail("alice@example.com");
		dto.setPassword("secret");
		dto.setLanguage(languageDto);
		dto.setRoles(Set.of(roleDto));
		dto.setEnabled(true);
		dto.setPhone("+32487964702");
		dto.setRegistrationDate(LocalDateTime.of(2025, 12, 31, 23, 59, 59, 0));
		dto.setValidationDate(LocalDateTime.of(2025, 12, 31, 23, 59, 59, 0));
		Admin entity = mapper.toEntity(dto);

		assertThat(entity).isInstanceOf(Admin.class);
		assertThat(entity.getFirstName()).isEqualTo("Alice");
		assertThat(entity.getLastName()).isEqualTo("Doe");
		assertThat(entity.getEmail()).isEqualTo("alice@example.com");
		assertThat(entity.getPassword()).isEqualTo("secret");
		assertThat(entity.getPhone()).isEqualTo("+32487964702");
		assertThat(entity.isEnabled()).isTrue();
		assertThat(entity.getRegistrationDate()).isEqualTo(LocalDateTime.of(2025, 12, 31, 23, 59, 59));
		assertThat(entity.getValidationDate()).isEqualTo(LocalDateTime.of(2025, 12, 31, 23, 59, 59));

		assertThat(entity.getLanguage()).isNotNull();
		assertThat(entity.getLanguage().getId()).isEqualTo(1);

		assertThat(entity.getRoles()).isNotEmpty();
		assertThat(entity.getRoles()).hasSize(1);
		assertThat(entity.getRoles().iterator().next().getId()).isEqualTo(10);
		assertThat(entity.getRoles().iterator().next().getName()).isEqualTo("Admin");
	}

	@Test
	void shouldMapAdminEntityToAdminDetailDto() {
		Language lang = new Language();
		lang.setId(1);

		Role role = new Role();
		role.setId(10);
		role.setName("ROLE_ADMIN");

		Admin admin = new Admin();
		admin.setFirstName("Bob");
		admin.setLastName("Smith");
		admin.setEmail("bob@example.com");
		admin.setPassword("supersecret");
		admin.setPhone("+32481234567");
		admin.setEnabled(true);
		admin.setLanguage(lang);
		admin.setRoles(Set.of(role));
		admin.setRegistrationDate(LocalDateTime.of(2024, 4, 12, 15, 30));
		admin.setValidationDate(LocalDateTime.of(2024, 4, 13, 10, 0));

		AdminDetailDto dto = mapper.toDto(admin);

		assertThat(dto).isInstanceOf(AdminDetailDto.class);

		assertThat(dto.getFirstName()).isEqualTo("Bob");
		assertThat(dto.getLastName()).isEqualTo("Smith");
		assertThat(dto.getEmail()).isEqualTo("bob@example.com");
		assertThat(dto.getPassword()).isEqualTo("supersecret");
		assertThat(dto.getPhone()).isEqualTo("+32481234567");
		assertThat(dto.getRegistrationDate()).isEqualTo(LocalDateTime.of(2024, 4, 12, 15, 30));
		assertThat(dto.getValidationDate()).isEqualTo(LocalDateTime.of(2024, 4, 13, 10, 0));

		assertThat(dto.getLanguage()).isNotNull();
		assertThat(dto.getLanguage().getId()).isEqualTo(1);

		assertThat(dto.getRoles()).isNotNull().isNotEmpty();
		assertThat(dto.getRoles()).hasSize(1);
		RoleDto roleDto = dto.getRoles().iterator().next();
		assertThat(roleDto.getId()).isEqualTo(10);
		assertThat(roleDto.getName()).isEqualTo("ROLE_ADMIN");
	}

	@Test
	void shouldPartiallyUpdateAdminEntity() {
		Language newLanguage = new Language();
		newLanguage.setId(2);

		LanguageDto languageDto = new LanguageDto();
		languageDto.setId(2);

		Admin existingAdmin = new Admin();
		existingAdmin.setFirstName("Old");
		existingAdmin.setLanguage(new Language());
		Set<Role> roles = new HashSet<>(Arrays.asList(new Role(1, "ADMIN"), new Role(2, "CARRIER")));

		existingAdmin.setRoles(roles);

		AdminDetailDto dto = new AdminDetailDto();
		dto.setFirstName("Updated");
		dto.setLanguage(languageDto);

		mapper.partialUpdate(dto, existingAdmin);

		assertThat(existingAdmin.getFirstName()).isEqualTo("Updated");
		assertThat(existingAdmin.getLanguage()).isNotNull();
		assertThat(existingAdmin.getRoles()).isEqualTo(roles);
	}

}
