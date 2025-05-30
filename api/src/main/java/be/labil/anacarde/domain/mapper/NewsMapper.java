package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.NewsDto;
import be.labil.anacarde.domain.model.News;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {MapperHelpers.class,
		NewsCategoryMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class NewsMapper {

	public abstract NewsDto toDto(News entity);

	public abstract News toEntity(NewsDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract News partialUpdate(NewsDto dto, @MappingTarget News entity);
}
