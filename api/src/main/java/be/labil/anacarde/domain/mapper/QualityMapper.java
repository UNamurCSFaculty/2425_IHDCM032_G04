package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.QualityDto;
import be.labil.anacarde.domain.dto.write.QualityUpdateDto;
import be.labil.anacarde.domain.model.*;
import jakarta.persistence.EntityManager;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {MapperHelpers.class,
		QualityTypeMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class QualityMapper {

	@Autowired
	protected EntityManager em;

	public abstract QualityDto toDto(Quality entity);

	@Mapping(target = "qualityType", ignore = true)
	public abstract Quality toEntity(QualityUpdateDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "qualityType", ignore = true)
	public abstract Quality partialUpdate(QualityUpdateDto dto, @MappingTarget Quality entity);

	@AfterMapping
	protected void afterUpdateDto(QualityUpdateDto dto, @MappingTarget Quality entity) {
		if (dto.getQualityTypeId() != null) {
			entity.setQualityType(em.getReference(QualityType.class, dto.getQualityTypeId()));
		}
	}
}
