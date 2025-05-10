package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.FieldService;
import be.labil.anacarde.domain.dto.db.FieldDto;
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
	public ResponseEntity<FieldDto> getField(Integer userId, Integer id) {
		FieldDto field = fieldService.getFieldById(id);
		return ResponseEntity.ok(field);
	}

	@Override
	public ResponseEntity<List<FieldDto>> listFields(Integer userId) {
		List<FieldDto> fields = fieldService.listFields(userId);
		return ResponseEntity.ok(fields);
	}

	@Override
	public ResponseEntity<FieldDto> createField(Integer userId, FieldDto fieldDto) {
		FieldDto created = fieldService.createField(fieldDto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(created.getId()).toUri();
		return ResponseEntity.created(location).body(created);
	}

	@Override
	public ResponseEntity<FieldDto> updateField(Integer userId, Integer id, FieldDto fieldDto) {
		FieldDto updated = fieldService.updateField(id, fieldDto);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<Void> deleteField(Integer userId, Integer id) {
		fieldService.deleteField(id);
		return ResponseEntity.noContent().build();
	}
}
