package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.LanguageDto;
import be.labil.anacarde.domain.mapper.LanguageMapper;
import be.labil.anacarde.domain.model.Language;
import be.labil.anacarde.infrastructure.persistence.LanguageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class LanguageServiceImpl implements LanguageService {

	LanguageRepository languageRepository;
	LanguageMapper languageMapper;

	@Override
	public LanguageDto create(LanguageDto languageDto) {
		Language language = languageMapper.toEntity(languageDto);
		Language saved = languageRepository.save(language);
		return languageMapper.toDto(saved);
	}
}
