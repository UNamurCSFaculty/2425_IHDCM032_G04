package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.BidStatusDto;
import be.labil.anacarde.domain.model.BidStatus;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class BidStatusMapper {

	public abstract BidStatusDto toDto(BidStatus status);

	public abstract BidStatus toEntity(BidStatusDto dto);
}
