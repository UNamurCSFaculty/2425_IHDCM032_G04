package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.GlobalSettingsService;
import be.labil.anacarde.domain.dto.db.GlobalSettingsDto;
import be.labil.anacarde.domain.dto.write.GlobalSettingsUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminGlobalSettingsApiController implements AdminGlobalSettingsApi {

	private final GlobalSettingsService globalSettingsService;

	@Override
	public ResponseEntity<GlobalSettingsDto> getGlobalSettings() {
		GlobalSettingsDto globalSettings = globalSettingsService.getGlobalSettings();
		return ResponseEntity.ok(globalSettings);
	}

	@Override
	public ResponseEntity<GlobalSettingsDto> updateGlobalSettings(
			GlobalSettingsUpdateDto globalSettingsUpdateDto) {
		GlobalSettingsDto updatedSettings = globalSettingsService
				.updateGlobalSettings(globalSettingsUpdateDto);
		return ResponseEntity.ok(updatedSettings);
	}
}
