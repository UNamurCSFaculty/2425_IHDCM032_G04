package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import be.labil.anacarde.domain.dto.LanguageDto;
import be.labil.anacarde.domain.dto.RoleDto;
import be.labil.anacarde.domain.dto.user.AdminDetailDto;
import be.labil.anacarde.domain.model.Admin;
import be.labil.anacarde.domain.model.Language;
import be.labil.anacarde.domain.model.Role;
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

		Admin entity = mapper.toEntity(dto);

		assertThat(entity).isInstanceOf(Admin.class);
		assertThat(entity.getFirstName()).isEqualTo("Alice");
		assertThat(entity.getRoles()).isNotEmpty();
		assertThat(entity.getLanguage()).isNotNull();
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
		admin.setLanguage(lang);
		admin.setRoles(Set.of(role));

		AdminDetailDto dto = mapper.toDto(admin);

		assertThat(dto).isInstanceOf(AdminDetailDto.class);
		assertThat(dto.getEmail()).isEqualTo("bob@example.com");
		assertThat(dto.getLanguage()).isNotNull();
		assertThat(dto.getRoles()).isNotEmpty();
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
		existingAdmin.setRoles(new HashSet<>()); // important

		AdminDetailDto dto = new AdminDetailDto();
		dto.setFirstName("Updated");
		dto.setLanguage(languageDto);

		mapper.partialUpdate(dto, existingAdmin);

		assertThat(existingAdmin.getFirstName()).isEqualTo("Updated");
		assertThat(existingAdmin.getLanguage()).isNotNull();
		assertThat(existingAdmin.getRoles()).isEmpty();
	}

}
