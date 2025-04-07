package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.UserDetailDto;
import be.labil.anacarde.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {RoleMapper.class,
		LanguageMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserDetailMapper extends GenericMapper<UserDetailDto, User> {

	@Override
	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	User toEntity(UserDetailDto dto);

	@Override
	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	UserDetailDto toDto(User user);
}
