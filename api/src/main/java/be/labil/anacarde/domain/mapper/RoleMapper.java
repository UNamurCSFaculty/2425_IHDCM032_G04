package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.RoleDto;
import be.labil.anacarde.domain.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper extends GenericMapper<RoleDto, Role> {

	@Override
	@Mapping(target = "users", ignore = true)
	Role toEntity(RoleDto dto);

	@Override
	RoleDto toDto(Role role);
}
