package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.NewsCategoryDto;
import be.labil.anacarde.domain.model.NewsCategory;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NewsCategoryMapper extends GenericMapper<NewsCategoryDto, NewsCategory> {

	@Override
	NewsCategoryDto toDto(NewsCategory entity);

	@Override
	NewsCategory toEntity(NewsCategoryDto dto);

	@Override
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	NewsCategory partialUpdate(NewsCategoryDto dto, @MappingTarget NewsCategory entity);
}
