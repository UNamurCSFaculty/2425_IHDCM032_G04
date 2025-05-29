package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.user.*;
import be.labil.anacarde.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
		MapperHelpers.class, AddressMapper.class, CooperativeMapper.class

})
public abstract class UserListMapper {

	/*---------------------------------------------------*/
	/* 1) Mapping Entité -> DTO pour chaque sous-classe */
	/*---------------------------------------------------*/

	public abstract AdminListDto toDto(Admin entity);

	public abstract ExporterListDto toDto(Exporter entity);

	public abstract CarrierListDto toDto(Carrier entity);

	public abstract QualityInspectorListDto toDto(QualityInspector entity);

	public abstract TransformerListDto toDto(Transformer entity);

	@Mapping(source = "agriculturalIdentifier", target = "agriculturalIdentifier")
	@Mapping(source = "cooperative.id", target = "cooperativeId")
	public abstract ProducerListDto toDto(Producer entity);

	/*---------------------------------------------------*/
	/* 2) Méthode générique de dispatch */
	/*---------------------------------------------------*/

	public UserListDto toDto(User entity) {
		if (entity instanceof Admin) {
			return toDto((Admin) entity);

		} else if (entity instanceof Exporter) {
			return toDto((Exporter) entity);

		} else if (entity instanceof Carrier) {
			return toDto((Carrier) entity);

		} else if (entity instanceof QualityInspector) {
			return toDto((QualityInspector) entity);

		} else if (entity instanceof Transformer) {
			return toDto((Transformer) entity);

		} else if (entity instanceof Producer) {
			return toDto((Producer) entity);
		}

		throw new IllegalArgumentException("Type de User non supporté : " + entity.getClass());
	}

	/*---------------------------------------------------*/
	/* 3) Mapping inverse non pertinent pour la liste */
	/*---------------------------------------------------*/

	public static User toEntity(UserListDto dto) {
		throw new UnsupportedOperationException(
				"Mapping from UserListDto to User is not supported.");
	}
}
