package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.LanguageDto;
import be.labil.anacarde.domain.model.Language;

public interface LanguageMapper extends GenericMapper<LanguageDto, Language> {

	@Override
	Language toEntity(LanguageDto dto);

	@Override
	LanguageDto toDto(Language entity);
}
