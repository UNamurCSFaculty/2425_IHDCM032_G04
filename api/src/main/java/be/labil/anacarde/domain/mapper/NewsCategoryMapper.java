package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.NewsCategoryDto;
import be.labil.anacarde.domain.model.NewsCategory;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class NewsCategoryMapper {

	public abstract NewsCategoryDto toDto(NewsCategory entity);

	public abstract NewsCategory toEntity(NewsCategoryDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract NewsCategory partialUpdate(NewsCategoryDto dto, @MappingTarget NewsCategory entity);
}
