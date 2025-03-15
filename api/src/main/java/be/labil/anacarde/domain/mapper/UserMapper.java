package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.UserDto;
import be.labil.anacarde.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/** Interface Mapper pour la conversion entre l'entité User et le DTO UserDto. */
@Mapper(componentModel = "spring", uses = {RoleMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends GenericMapper<UserDto, User> {
}
