package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.QualityTypeDto;
import be.labil.anacarde.domain.model.QualityType;
import org.mapstruct.*;

/**
 * Mapper MapStruct pour l'entité QualityType et son DTO.
 */
@Mapper(componentModel = "spring", uses = {
		MapperHelpers.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QualityTypeMapper {

	QualityTypeDto toDto(QualityType entity);

	QualityType toEntity(QualityTypeDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	QualityType partialUpdate(QualityTypeDto dto, @MappingTarget QualityType entity);
}