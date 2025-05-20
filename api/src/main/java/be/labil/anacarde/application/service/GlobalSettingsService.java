package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.db.GlobalSettingsDto;
import be.labil.anacarde.domain.dto.write.GlobalSettingsUpdateDto;

public interface GlobalSettingsService {
	GlobalSettingsDto getGlobalSettings();
	GlobalSettingsDto updateGlobalSettings(GlobalSettingsUpdateDto dto);
}
