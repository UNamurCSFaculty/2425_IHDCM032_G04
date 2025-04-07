package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.UserDto;
import be.labil.anacarde.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper extends GenericMapper<UserDto, User> {

	@Override
	@Mapping(source = "roles", target = "roles")
	User toEntity(UserDto dto);

	@Override
	@Mapping(source = "roles", target = "roles")
	UserDto toDto(User user);
}
