package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.ApplicationDataService;
import be.labil.anacarde.domain.dto.ApplicationDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApplicationDataController implements ApplicationData {

	private final ApplicationDataService applicationDataService;

	@Override
	public ApplicationDataDto getApplicationData() {
		return applicationDataService.getApplicationData();
	}
}
