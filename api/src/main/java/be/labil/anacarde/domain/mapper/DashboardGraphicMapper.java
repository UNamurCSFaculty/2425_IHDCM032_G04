package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.view.DashboardGraphicDto;
import be.labil.anacarde.domain.model.DashboardGraphic;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for the read-only view {@link DashboardGraphic} and its DTO
 * {@link DashboardGraphicDto}.
 *
 * <p>
 * Aucune annotation {@code @Mapping} nécessaire : les noms de propriétés coïncident exactement.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface DashboardGraphicMapper {

	/** Conversion entité ➜ DTO. */
	DashboardGraphicDto toDto(DashboardGraphic entity);

	/** Conversion liste d’entités ➜ liste de DTO (utile pour les séries). */
	java.util.List<DashboardGraphicDto> toDtoList(java.util.List<DashboardGraphic> entities);
}
