package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.db.LanguageDto;
import be.labil.anacarde.domain.mapper.LanguageMapper;
import be.labil.anacarde.domain.model.Language;
import be.labil.anacarde.infrastructure.persistence.LanguageRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class LanguageServiceImpl implements LanguageService {

	private final LanguageRepository languageRepository;
	private final LanguageMapper languageMapper;

	@Override
	public LanguageDto createLanguage(LanguageDto languageDto) {
		Language language = languageMapper.toEntity(languageDto);
		Language saved = languageRepository.save(language);
		return languageMapper.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public LanguageDto getLanguageById(Integer id) {
		Language language = languageRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Langue non trouvée pour l'id " + id));
		return languageMapper.toDto(language);
	}

	@Override
	@Transactional(readOnly = true)
	public List<LanguageDto> listLanguages() {
		return languageRepository.findAll().stream().map(languageMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public LanguageDto updateLanguage(Integer id, LanguageDto languageDto) {
		Language existing = languageRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Langue non trouvée pour l'id " + id));
		// Met à jour les champs pertinents
		existing.setName(languageDto.getName());
		Language updated = languageRepository.save(existing);
		return languageMapper.toDto(updated);
	}

	@Override
	public void deleteLanguage(Integer id) {
		if (!languageRepository.existsById(id)) {
			throw new ResourceNotFoundException("Langue non trouvée pour l'id " + id);
		}
		languageRepository.deleteById(id);
	}
}
