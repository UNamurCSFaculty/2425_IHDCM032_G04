package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.NewsDto;
import be.labil.anacarde.domain.dto.write.NewsCreateDto;
import be.labil.anacarde.domain.dto.write.NewsUpdateDto;
import be.labil.anacarde.domain.model.*;
import jakarta.persistence.EntityManager;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {MapperHelpers.class,
		NewsCategoryMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class NewsMapper {

	@Autowired
	protected EntityManager em;

	@Mapping(target = "category", source = "category")
	@Mapping(source = "authorName", target = "authorName")
	public abstract NewsDto toDto(News entity);

	@Mapping(target = "category", ignore = true)
	@Mapping(source = "authorName", target = "authorName")
	public abstract News toEntity(NewsCreateDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "category", ignore = true)
	@Mapping(source = "authorName", target = "authorName")
	public abstract News partialUpdate(NewsUpdateDto dto, @MappingTarget News entity);

	@AfterMapping
	protected void afterUpdateDto(NewsCreateDto dto, @MappingTarget News entity) {
		if (dto.getCategoryId() != null) {
			entity.setCategory(em.getReference(NewsCategory.class, dto.getCategoryId()));
		}
	}

	@AfterMapping
	protected void afterUpdateDto(NewsUpdateDto dto, @MappingTarget News entity) {
		if (dto.getCategoryId() != null) {
			entity.setCategory(em.getReference(NewsCategory.class, dto.getCategoryId()));
		}
	}
}
