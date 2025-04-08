package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.NewsDto;
import be.labil.anacarde.domain.model.News;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {NewsCategoryMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NewsMapper extends GenericMapper<NewsDto, News> {

	@Override
	@Mapping(source = "category", target = "category")
	NewsDto toDto(News entity);

	@Override
	@Mapping(source = "category", target = "category")
	News toEntity(NewsDto dto);

	@Override
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "category", target = "category")
	News partialUpdate(NewsDto dto, @MappingTarget News entity);
}
