package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.user.*;
import be.labil.anacarde.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserListMapper extends GenericMapper<UserListDto, User> {

	@Override
	@Mapping(target = "password", ignore = true) // le mot de passe n'est utilisé qu'à l'écriture, on l'ignore ici
	UserListDto toDto(User user);

	@ObjectFactory
	default UserListDto createUserDto(User user) {
		if (user instanceof Admin) {
			return new AdminListDto();
		} else if (user instanceof Exporter) {
			return new ExporterListDto();
		} else if (user instanceof Carrier) {
			return new CarrierListDto();
		} else if (user instanceof QualityInspector) {
			return new QualityInspectorListDto();
		} else if (user instanceof Producer) {
			return new ProducerListDto();
		} else if (user instanceof Transformer) {
			return new TransformerListDto();
		}

		throw new IllegalArgumentException("Type de User non supporté : " + user.getClass());
	}

	@Override
	default User toEntity(UserListDto dto) {
		// Si le mapping depuis le DTO vers l'entité n'est pas nécessaire pour une liste,
		// vous pouvez lever une exception ou retourner null.
		throw new UnsupportedOperationException("Mapping from UserListDto to User is not supported.");
	}
}
