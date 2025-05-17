package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.QualityTypeDto;
import be.labil.anacarde.domain.model.QualityType;
import org.mapstruct.*;

/**
 * Mapper MapStruct pour l'entité QualityType et son DTO.
 */
@Mapper(componentModel = "spring", uses = {
		MapperHelpers.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class QualityTypeMapper {

	public abstract QualityTypeDto toDto(QualityType entity);

	public abstract QualityType toEntity(QualityTypeDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract QualityType partialUpdate(QualityTypeDto dto,
			@MappingTarget QualityType entity);
}