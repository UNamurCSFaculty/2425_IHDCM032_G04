package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.RoleDto;
import be.labil.anacarde.domain.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @brief Mapper interface for converting between Role entity and RoleDto.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper extends GenericMapper<RoleDto, Role> {

}
