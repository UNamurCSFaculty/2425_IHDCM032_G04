package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.LanguageDto;
import be.labil.anacarde.domain.model.Language;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class LanguageMapper {

	public abstract Language toEntity(LanguageDto dto);

	public abstract LanguageDto toDto(Language entity);
}
