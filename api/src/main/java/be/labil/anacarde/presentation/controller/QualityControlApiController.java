package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.QualityControlService;
import be.labil.anacarde.domain.dto.db.QualityControlDto;
import be.labil.anacarde.domain.dto.write.QualityControlUpdateDto;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class QualityControlApiController implements QualityControlApi {

	private final QualityControlService qualityControlService;

	@Override
	public ResponseEntity<? extends QualityControlDto> getQualityControl(Integer id) {
		QualityControlDto qc = qualityControlService.getQualityControlById(id);
		return ResponseEntity.ok(qc);
	}

	@Override
	public ResponseEntity<List<? extends QualityControlDto>> listQualityControls() {
		List<QualityControlDto> list = qualityControlService.listQualityControls();
		return ResponseEntity.ok(list);
	}

	@Override
	public ResponseEntity<QualityControlDto> createQualityControl(
			QualityControlUpdateDto qualityControlDto) {
		QualityControlDto created = qualityControlService.createQualityControl(qualityControlDto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(created.getId()).toUri();
		return ResponseEntity.created(location).body(created);
	}

	@Override
	public ResponseEntity<QualityControlDto> updateQualityControl(Integer id,
			QualityControlUpdateDto qualityControlDto) {
		QualityControlDto updated = qualityControlService.updateQualityControl(id,
				qualityControlDto);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<Void> deleteQualityControl(Integer id) {
		qualityControlService.deleteQualityControl(id);
		return ResponseEntity.noContent().build();
	}
}
