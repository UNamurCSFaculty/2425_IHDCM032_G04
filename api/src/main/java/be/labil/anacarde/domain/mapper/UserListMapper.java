package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.UserListDto;
import be.labil.anacarde.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserListMapper extends GenericMapper<UserListDto, User> {

	@Override
	@Mapping(target = "password", ignore = true) // le mot de passe n'est utilisé qu'à l'écriture, on l'ignore ici
	UserListDto toDto(User user);

	@Override
	default User toEntity(UserListDto dto) {
		// Si le mapping depuis le DTO vers l'entité n'est pas nécessaire pour une liste,
		// vous pouvez lever une exception ou retourner null.
		throw new UnsupportedOperationException("Mapping from UserListDto to User is not supported.");
	}
}
