package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.TranslationDto;
import be.labil.anacarde.domain.model.Translation;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TranslationMapper {

	public abstract Translation toEntity(TranslationDto dto);

	public abstract TranslationDto toDto(Translation entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract Translation partialUpdate(TranslationDto dto, @MappingTarget Translation entity);
}
