package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.export.DashboardService;
import be.labil.anacarde.domain.dto.db.view.DashboardCardsDto;
import be.labil.anacarde.domain.dto.db.view.DashboardGraphicDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implémentation REST des end-points Dashboard.
 */
@RestController
@RequiredArgsConstructor
public class DashboardApiController implements DashboardApi {

	private final DashboardService dashboardService;

	/* ===== KPI « cards » ===== */
	@Override
	public ResponseEntity<DashboardCardsDto> getDashboardCards() {
		return ResponseEntity.ok(dashboardService.getDashboardStats());
	}

	/* ===== Série chronologique ===== */
	@Override
	public ResponseEntity<List<DashboardGraphicDto>> getDashboardGraphicSeries() {
		return ResponseEntity.ok(dashboardService.getDashboardGraphicSeries());
	}
}
