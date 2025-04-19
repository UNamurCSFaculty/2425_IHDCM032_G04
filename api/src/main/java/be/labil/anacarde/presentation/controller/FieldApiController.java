package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.FieldService;
import be.labil.anacarde.domain.dto.FieldDetailDto;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class FieldApiController implements FieldApi {

	private final FieldService fieldService;

	@Override
	public ResponseEntity<? extends FieldDetailDto> getField(Integer id) {
		FieldDetailDto field = fieldService.getFieldById(id);
		return ResponseEntity.ok(field);
	}

	@Override
	public ResponseEntity<List<? extends FieldDetailDto>> listFields(Integer userId) {
		List<FieldDetailDto> fields = fieldService.listFields(userId);
		return ResponseEntity.ok(fields);
	}

	@Override
	public ResponseEntity<FieldDetailDto> createField(FieldDetailDto fieldDetailDto) {
		FieldDetailDto created = fieldService.createField(fieldDetailDto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId())
				.toUri();
		return ResponseEntity.created(location).body(created);
	}

	@Override
	public ResponseEntity<FieldDetailDto> updateField(Integer id, FieldDetailDto fieldDetailDto) {
		FieldDetailDto updated = fieldService.updateField(id, fieldDetailDto);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<Void> deleteField(Integer id) {
		fieldService.deleteField(id);
		return ResponseEntity.noContent().build();
	}
}
