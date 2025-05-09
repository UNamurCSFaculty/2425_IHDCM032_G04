package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.QualityDto;
import be.labil.anacarde.domain.model.Quality;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class QualityMapper {

	public abstract QualityDto toDto(Quality entity);

	public abstract Quality toEntity(QualityDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract Quality partialUpdate(QualityDto dto, @MappingTarget Quality entity);
}
