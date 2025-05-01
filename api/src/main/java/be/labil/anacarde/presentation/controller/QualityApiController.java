package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.QualityService;
import be.labil.anacarde.domain.dto.QualityDto;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class QualityApiController implements QualityApi {
	private final QualityService qualityService;

	@Override
	public ResponseEntity<QualityDto> getQuality(Integer id) {
		QualityDto quality = qualityService.getQualityById(id);
		return ResponseEntity.ok(quality);
	}

	@Override
	public ResponseEntity<List<QualityDto>> listQualities() {
		List<QualityDto> qualitys = qualityService.listQualities();
		return ResponseEntity.ok(qualitys);
	}

	@Override
	public ResponseEntity<QualityDto> createQuality(QualityDto qualityDetailDto) {
		QualityDto created = qualityService.createQuality(qualityDetailDto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId())
				.toUri();
		return ResponseEntity.created(location).body(created);
	}

	@Override
	public ResponseEntity<QualityDto> updateQuality(Integer id, QualityDto qualityDetailDto) {
		QualityDto updated = qualityService.updateQuality(id, qualityDetailDto);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<Void> deleteQuality(Integer id) {
		qualityService.deleteQuality(id);
		return ResponseEntity.noContent().build();
	}
}
