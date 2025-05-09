package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.RoleDto;
import be.labil.anacarde.domain.model.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class RoleMapper {

	public abstract Role toEntity(RoleDto dto);

	public abstract RoleDto toDto(Role role);
}
