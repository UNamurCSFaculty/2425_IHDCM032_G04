package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.view.DashboardCardsDto;
import be.labil.anacarde.domain.model.DashboardCards;
import org.mapstruct.Mapper;

/**
 * Conversion simple View → DTO.
 */
@Mapper(componentModel = "spring")
public interface DashboardCardsMapper {

	DashboardCardsDto toDto(DashboardCards view);
}