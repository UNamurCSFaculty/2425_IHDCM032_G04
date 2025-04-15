package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import be.labil.anacarde.domain.dto.RoleDto;
import be.labil.anacarde.domain.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RoleMapperTest {

	@Autowired
	private RoleMapper roleMapper;

	@Test
	void shouldMapEntityToDto() {
		Role entity = new Role();
		entity.setId(1);
		entity.setName("ROLE_USER");

		RoleDto dto = roleMapper.toDto(entity);

		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(entity.getId());
		assertThat(dto.getName()).isEqualTo(entity.getName());
	}

	@Test
	void shouldMapDtoToEntity() {
		RoleDto dto = new RoleDto(1, "ROLE_USER");

		Role entity = roleMapper.toEntity(dto);

		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isEqualTo(dto.getId());
		assertThat(entity.getName()).isEqualTo(dto.getName());
	}

	@Test
	void shouldHandleNullValuesInDto() {
		RoleDto dto = new RoleDto(null, null);

		Role entity = roleMapper.toEntity(dto);

		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isNull();
		assertThat(entity.getName()).isNull();
	}

	@Test
	void shouldHandleNullValuesInEntity() {
		Role entity = new Role();
		entity.setId(null);
		entity.setName(null);

		RoleDto dto = roleMapper.toDto(entity);

		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isNull();
		assertThat(dto.getName()).isNull();
	}
}
