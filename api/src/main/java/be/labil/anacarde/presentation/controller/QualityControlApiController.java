package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorCode;
import be.labil.anacarde.application.exception.ApiErrorException;
import be.labil.anacarde.application.exception.ErrorDetail;
import be.labil.anacarde.application.service.QualityControlService;
import be.labil.anacarde.domain.dto.db.QualityControlDto;
import be.labil.anacarde.domain.dto.write.QualityControlUpdateDto;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
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
			QualityControlUpdateDto qualityControlDto, List<MultipartFile> documents) {
		if (documents == null || documents.isEmpty()) {
			throw new ApiErrorException(HttpStatus.BAD_REQUEST, ApiErrorCode.BAD_REQUEST.code(),
					List.of(new ErrorDetail("documents", "qualityControl.documents.required",
							"Le champ documents est requis.")));
		}

		QualityControlDto created = qualityControlService.createQualityControl(qualityControlDto,
				documents);
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
