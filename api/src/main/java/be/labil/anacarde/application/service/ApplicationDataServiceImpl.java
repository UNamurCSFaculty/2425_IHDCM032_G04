package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.ApplicationDataDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class ApplicationDataServiceImpl implements ApplicationDataService {

	private final LanguageService languageService;

	@Override
	public ApplicationDataDto getApplicationData() {
		return ApplicationDataDto.builder().languages(languageService.listLanguages()).build();
	}
}
