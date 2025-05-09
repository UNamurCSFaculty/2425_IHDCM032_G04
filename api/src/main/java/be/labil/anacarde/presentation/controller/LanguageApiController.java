package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.LanguageService;
import be.labil.anacarde.domain.dto.db.LanguageDto;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class LanguageApiController implements LanguageApi {

	private final LanguageService languageService;

	@Override
	public ResponseEntity<LanguageDto> getLanguage(Integer id) {
		LanguageDto dto = languageService.getLanguageById(id);
		return ResponseEntity.ok(dto);
	}

	@Override
	public ResponseEntity<LanguageDto> createLanguage(LanguageDto languageDto) {
		LanguageDto created = languageService.createLanguage(languageDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@Override
	public ResponseEntity<List<LanguageDto>> listLanguages() {
		List<LanguageDto> langs = languageService.listLanguages();
		return ResponseEntity.ok(langs);
	}

	@Override
	public ResponseEntity<LanguageDto> updateLanguage(Integer id, LanguageDto languageDto) {
		LanguageDto updated = languageService.updateLanguage(id, languageDto);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<Void> deleteLanguage(Integer id) {
		languageService.deleteLanguage(id);
		return ResponseEntity.noContent().build();
	}
}
