package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.CooperativeService;
import be.labil.anacarde.domain.dto.CooperativeDto;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class CooperativeApiController implements CooperativeApi {

	private final CooperativeService cooperativeService;

	@Override
	public ResponseEntity<? extends CooperativeDto> getCooperative(Integer id) {
		CooperativeDto cooperative = cooperativeService.getCooperativeById(id);
		return ResponseEntity.ok(cooperative);
	}

	@Override
	public ResponseEntity<List<? extends CooperativeDto>> listCooperatives() {
		List<CooperativeDto> cooperatives = cooperativeService.listCooperatives();
		return ResponseEntity.ok(cooperatives);
	}

	@Override
	public ResponseEntity<CooperativeDto> createCooperative(CooperativeDto cooperativeDto) {
		CooperativeDto created = cooperativeService.createCooperative(cooperativeDto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId())
				.toUri();
		return ResponseEntity.created(location).body(created);
	}

	@Override
	public ResponseEntity<CooperativeDto> updateCooperative(Integer id, CooperativeDto cooperativeDto) {
		CooperativeDto updated = cooperativeService.updateCooperative(id, cooperativeDto);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<Void> deleteCooperative(Integer id) {
		cooperativeService.deleteCooperative(id);
		return ResponseEntity.noContent().build();
	}
}
