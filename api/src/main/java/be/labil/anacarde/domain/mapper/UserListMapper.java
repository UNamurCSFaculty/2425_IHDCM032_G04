package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.user.*;
import be.labil.anacarde.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = MapperHelpers.class)
public abstract class UserListMapper {

	public abstract UserListDto toDto(User user);

	@ObjectFactory
	public static UserListDto createUserDto(User user) {
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

	public static User toEntity(UserListDto dto) {
		// Si le mapping depuis le DTO vers l'entité n'est pas nécessaire pour une liste,
		// vous pouvez lever une exception ou retourner null.
		throw new UnsupportedOperationException(
				"Mapping from UserListDto to User is not supported.");
	}
}
