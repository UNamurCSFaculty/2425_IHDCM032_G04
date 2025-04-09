package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.BidStatusDto;
import be.labil.anacarde.domain.model.BidStatus;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BidStatusMapper extends GenericMapper<BidStatusDto, BidStatus> {

	@Override
	BidStatusDto toDto(BidStatus status);

	@Override
	BidStatus toEntity(BidStatusDto dto);
}
