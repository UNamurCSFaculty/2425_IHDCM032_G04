package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.db.GlobalSettingsDto;
import be.labil.anacarde.domain.dto.write.GlobalSettingsUpdateDto;
import be.labil.anacarde.domain.mapper.GlobalSettingsMapper;
import be.labil.anacarde.domain.model.GlobalSettings;
import be.labil.anacarde.infrastructure.persistence.GlobalSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class GlobalSettingsServiceImpl implements GlobalSettingsService {

	private static final Integer SINGLETON_ID = 1;

	private final GlobalSettingsRepository globalSettingsRepository;
	private final GlobalSettingsMapper globalSettingsMapper;

	@Override
	@Transactional(readOnly = true)
	public GlobalSettingsDto getGlobalSettings() {
		GlobalSettings gs = globalSettingsRepository.findById(SINGLETON_ID).orElseThrow(
				() -> new ResourceNotFoundException("Réglages globaux non configurés"));
		return globalSettingsMapper.toDto(gs);
	}

	@Override
	public GlobalSettingsDto updateGlobalSettings(GlobalSettingsUpdateDto dto) {
		GlobalSettings toSave = globalSettingsMapper.toEntity(dto);
		toSave.setId(1);
		GlobalSettings saved = globalSettingsRepository.save(toSave);
		return globalSettingsMapper.toDto(saved);
	}
}