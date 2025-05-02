package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.RegionDto;
import be.labil.anacarde.domain.model.Region;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class RegionMapper {

	public abstract Region toEntity(RegionDto dto);

	public abstract RegionDto toDto(Region entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract Region partialUpdate(RegionDto dto, @MappingTarget Region entity);
}
