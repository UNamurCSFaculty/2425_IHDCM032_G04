package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.UserListDto;
import be.labil.anacarde.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/** Interface Mapper pour la conversion entre l'entité User et le DTO UserListDto. */
@Mapper(componentModel = "spring", uses = {RoleMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserListMapper extends GenericMapper<UserListDto, User> {
}