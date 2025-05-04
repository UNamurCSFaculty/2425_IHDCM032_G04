package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.BidStatusDto;
import be.labil.anacarde.domain.model.BidStatus;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class BidStatusMapper {

	public abstract BidStatusDto toDto(BidStatus status);

	public abstract BidStatus toEntity(BidStatusDto dto);

	@Mapping(source = "name", target = "name")
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract BidStatus partialUpdate(BidStatusDto documentDto, @MappingTarget BidStatus entity);
}
