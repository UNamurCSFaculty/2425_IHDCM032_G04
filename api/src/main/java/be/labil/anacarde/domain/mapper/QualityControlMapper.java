package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.QualityControlDto;
import be.labil.anacarde.domain.dto.write.QualityControlUpdateDto;
import be.labil.anacarde.domain.model.Quality;
import be.labil.anacarde.domain.model.QualityControl;
import be.labil.anacarde.domain.model.QualityInspector;
import jakarta.persistence.EntityManager;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {MapperHelpers.class, UserDetailMapper.class,
		ProductMapper.class, QualityMapper.class, UserMiniMapper.class,
		DocumentMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class QualityControlMapper {

	@Autowired
	protected EntityManager em;

	@Mapping(source = "qualityInspector", target = "qualityInspector")
	@Mapping(source = "quality", target = "quality")
	@Mapping(source = "documents", target = "documents")
	public abstract QualityControlDto toDto(QualityControl entity);

	@Mapping(target = "qualityInspector", ignore = true)
	@Mapping(target = "quality", ignore = true)
	@Mapping(target = "documents", ignore = true)
	public abstract QualityControl toEntity(QualityControlUpdateDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "qualityInspector", ignore = true)
	@Mapping(target = "quality", ignore = true)
	@Mapping(target = "documents", ignore = true)
	public abstract QualityControl partialUpdate(QualityControlUpdateDto dto,
			@MappingTarget QualityControl entity);

	@AfterMapping
	protected void afterUpdateDto(QualityControlUpdateDto dto, @MappingTarget QualityControl c) {
		if (dto.getQualityInspectorId() != null) {
			c.setQualityInspector(
					em.getReference(QualityInspector.class, dto.getQualityInspectorId()));
		}
		if (dto.getQualityId() != null) {
			c.setQuality(em.getReference(Quality.class, dto.getQualityId()));
		}
	}
}
