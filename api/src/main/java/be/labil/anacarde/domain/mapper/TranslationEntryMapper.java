package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.TranslationEntryDto;
import be.labil.anacarde.domain.model.Language;
import be.labil.anacarde.domain.model.Translation;
import be.labil.anacarde.domain.model.TranslationEntry;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TranslationEntryMapper extends GenericMapper<TranslationEntryDto, TranslationEntry> {

	@Override
	@Mapping(source = "translation.id", target = "translationId")
	@Mapping(source = "language.id", target = "languageId")
	TranslationEntryDto toDto(TranslationEntry entity);

	@Override
	@Mapping(source = "translationId", target = "translation", qualifiedByName = "mapTranslationId")
	@Mapping(source = "languageId", target = "language", qualifiedByName = "mapLanguageId")
	TranslationEntry toEntity(TranslationEntryDto dto);

	@Override
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "translationId", target = "translation", qualifiedByName = "mapTranslationId")
	@Mapping(source = "languageId", target = "language", qualifiedByName = "mapLanguageId")
	TranslationEntry partialUpdate(TranslationEntryDto dto, @MappingTarget TranslationEntry entity);

	@Named("mapTranslationId")
	default Translation mapTranslationId(Integer id) {
		if (id == null) return null;
		Translation translation = new Translation();
		translation.setId(id);
		return translation;
	}

	@Named("mapLanguageId")
	default Language mapLanguageId(Integer id) {
		if (id == null) return null;
		Language language = new Language();
		language.setId(id);
		return language;
	}
}
