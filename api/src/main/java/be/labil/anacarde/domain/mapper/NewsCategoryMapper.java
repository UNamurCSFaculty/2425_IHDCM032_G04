package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.NewsCategoryDto;
import be.labil.anacarde.domain.model.NewsCategory;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class NewsCategoryMapper {

	@Mapping(source = "description", target = "description")
	public abstract NewsCategoryDto toDto(NewsCategory entity);

	@Mapping(source = "description", target = "description")
	public abstract NewsCategory toEntity(NewsCategoryDto dto);

	@Mapping(source = "description", target = "description")
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract NewsCategory partialUpdate(NewsCategoryDto dto,
			@MappingTarget NewsCategory entity);
}
