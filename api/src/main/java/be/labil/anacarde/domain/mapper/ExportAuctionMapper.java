package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.view.ExportAuctionDto;
import be.labil.anacarde.domain.model.ExportAuction;
import org.mapstruct.Mapper;

/**
 * Conversion simple View → DTO.
 */
@Mapper(componentModel = "spring")
public interface ExportAuctionMapper {

	ExportAuctionDto toDto(ExportAuction view);
}
