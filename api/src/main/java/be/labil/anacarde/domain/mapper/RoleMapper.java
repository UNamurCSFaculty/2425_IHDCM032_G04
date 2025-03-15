package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.RoleDto;
import be.labil.anacarde.domain.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/** Interface Mapper pour la conversion entre l'entité Role et le DTO RoleDto. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper extends GenericMapper<RoleDto, Role> {
}
