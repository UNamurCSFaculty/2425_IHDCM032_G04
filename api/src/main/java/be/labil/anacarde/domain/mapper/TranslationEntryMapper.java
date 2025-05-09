package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.TranslationEntryDto;
import be.labil.anacarde.domain.model.Language;
import be.labil.anacarde.domain.model.Translation;
import be.labil.anacarde.domain.model.TranslationEntry;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TranslationEntryMapper {

	@Mapping(source = "translation.id", target = "translationId")
	@Mapping(source = "language.id", target = "languageId")
	public abstract TranslationEntryDto toDto(TranslationEntry entity);

	@Mapping(source = "translationId", target = "translation", qualifiedByName = "mapTranslationId")
	@Mapping(source = "languageId", target = "language", qualifiedByName = "mapLanguageId")
	public abstract TranslationEntry toEntity(TranslationEntryDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "translationId", target = "translation", qualifiedByName = "mapTranslationId")
	@Mapping(source = "languageId", target = "language", qualifiedByName = "mapLanguageId")
	public abstract TranslationEntry partialUpdate(TranslationEntryDto dto, @MappingTarget TranslationEntry entity);

	@Named("mapTranslationId")
	public static Translation mapTranslationId(Integer id) {
		if (id == null) return null;
		Translation translation = new Translation();
		translation.setId(id);
		return translation;
	}

	@Named("mapLanguageId")
	public static Language mapLanguageId(Integer id) {
		if (id == null) return null;
		Language language = new Language();
		language.setId(id);
		return language;
	}
}
