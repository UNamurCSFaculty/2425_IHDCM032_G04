package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.view.DashboardCardsDto;
import be.labil.anacarde.domain.dto.db.view.DashboardGraphicDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * End-points « Dashboard » (indicateurs globaux & série chronologique).
 */
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/dashboard", produces = "application/json")
@Tag(name = "dashboard", description = "KPI et graphiques du tableau de bord")
public interface DashboardApi {

	/* ------------------------------------------------------------------ */
	/* 1. Cartes KPI (vue v_dashboard_cards) */
	/* ------------------------------------------------------------------ */
	@Operation(summary = "Obtenir la carte des indicateurs globaux")
	@GetMapping("/cards")
	@ApiResponses({
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = DashboardCardsDto.class))),
			@ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<DashboardCardsDto> getDashboardCards();

	/* ------------------------------------------------------------------ */
	/* 2. Série chronologique (vue v_dashboard_graphic) */
	/* ------------------------------------------------------------------ */
	@Operation(summary = "Obtenir la série chronologique « Open vs New »")
	@GetMapping("/graphic")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée", content = @Content(array = @ArraySchema(schema = @Schema(implementation = DashboardGraphicDto.class))))})
	ResponseEntity<List<DashboardGraphicDto>> getDashboardGraphicSeries();
}
