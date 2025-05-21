package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.export.ExportService;
import be.labil.anacarde.domain.dto.db.view.ExportAuctionDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExportApiController implements ExportApi {

	private final ExportService exportService;

	@Override
	public ResponseEntity<ExportAuctionDto> getAuction(Integer id) {
		return ResponseEntity.ok(exportService.getAuctionById(id));
	}

	@Override
	public ResponseEntity<List<ExportAuctionDto>> listAuctions(LocalDateTime start,
			LocalDateTime end, boolean onlyEnded) {
		return ResponseEntity.ok(exportService.getAnalysesBetween(start, end, onlyEnded));
	}
}
