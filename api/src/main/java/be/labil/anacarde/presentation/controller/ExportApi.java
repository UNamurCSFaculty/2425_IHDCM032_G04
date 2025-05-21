package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.view.ExportAuctionDto;
import be.labil.anacarde.presentation.controller.annotations.ApiValidId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/export/auctions", produces = "application/json")
@Tag(name = "export-auctions", description = "Vue analytique des enchères pour l’export")
public interface ExportApi {

	/* ------------------------------------------------------------------ */
	/* 1. Lecture d’une ligne de la vue */
	/* ------------------------------------------------------------------ */
	@Operation(summary = "Obtenir l’analyse d’une enchère")
	@GetMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ExportAuctionDto.class))),
			@ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<ExportAuctionDto> getAuction(@ApiValidId @PathVariable("id") Integer id);

	/* ------------------------------------------------------------------ */
	/* 2. Lecture filtrée entre deux dates */
	/* ------------------------------------------------------------------ */
	@Operation(summary = "Lister les enchères entre deux dates (optionnellement terminées)")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ExportAuctionDto.class))))})
	ResponseEntity<List<ExportAuctionDto>> listAuctions(
			@Parameter(description = "Date/heure de début (inclus)") @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,

			@Parameter(description = "Date/heure de fin (inclus)") @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,

			@Parameter(description = "true → ne renvoyer que les enchères terminées") @RequestParam(name = "onlyEnded", defaultValue = "false") boolean onlyEnded);
}
