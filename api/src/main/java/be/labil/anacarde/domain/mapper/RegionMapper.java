package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.RegionDto;
import be.labil.anacarde.domain.model.Region;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RegionMapper extends GenericMapper<RegionDto, Region> {

	@Override
	Region toEntity(RegionDto dto);

	@Override
	RegionDto toDto(Region entity);

	@Override
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	Region partialUpdate(RegionDto dto, @MappingTarget Region entity);
}
