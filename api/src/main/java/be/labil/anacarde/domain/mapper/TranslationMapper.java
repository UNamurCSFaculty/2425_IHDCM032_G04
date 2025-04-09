package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.TranslationDto;
import be.labil.anacarde.domain.model.Translation;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TranslationMapper extends GenericMapper<TranslationDto, Translation> {

	@Override
	Translation toEntity(TranslationDto dto);

	@Override
	TranslationDto toDto(Translation entity);

	@Override
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	Translation partialUpdate(TranslationDto dto, @MappingTarget Translation entity);
}
