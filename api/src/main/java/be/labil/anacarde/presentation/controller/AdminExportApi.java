package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * End-points « Export des données d'enchères » (API de Business Intelligence pour l'export de
 * données d'enchères anonymisées et enrichies).
 */
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/admin/export/auctions")
@Tag(name = "admin")
public interface AdminExportApi {

	/**
	 * Exporte les données d'analyse BI des enchères sous forme de fichier JSON ou CSV. Les données
	 * peuvent être filtrées par période (start/end) et par statut (onlyEnded). Si start et end ne
	 * sont pas fournis, toutes les données sont retournées. Le format de sortie peut être JSON (par
	 * défaut) ou CSV.
	 *
	 * @param start
	 *            Date/heure de début de la période d'analyse (inclus, optionnel).
	 * @param end
	 *            Date/heure de fin de la période d'analyse (inclus, optionnel).
	 * @param onlyEnded
	 *            Si vrai, ne retourne que les enchères terminées (optionnel, défaut false).
	 * @param format
	 *            Format du fichier exporté : "json" ou "csv" (optionnel, défaut "json").
	 * @return ResponseEntity<Resource> permettant le téléchargement du fichier de données (JSON ou
	 *         CSV).
	 */
	@Operation(summary = "Exporter et télécharger les données d'analyse BI des enchères (JSON/CSV)", description = "Permet de récupérer toutes les données ou de filtrer par période. Le résultat est un fichier (JSON ou CSV) à télécharger.")
	@GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, "text/csv"})
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Fichier de données (JSON ou CSV) généré et prêt pour le téléchargement.", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(type = "string", format = "binary", description = "Fichier JSON contenant les données d'enchères.")),
					@Content(mediaType = "text/csv", schema = @Schema(type = "string", format = "binary", description = "Fichier CSV contenant les données d'enchères."))}),
			@ApiResponse(responseCode = "400", description = "Paramètres de requête invalides.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erreur interne du serveur.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<Resource> getFilteredData( // Type de retour modifié
			@Parameter(description = "Date/heure de début de la période d'analyse (inclus). Laisser vide avec 'end' pour récupérer toutes les données.") @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,

			@Parameter(description = "Date/heure de fin de la période d'analyse (inclus). Laisser vide avec 'start' pour récupérer toutes les données.") @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,

			@Parameter(description = "Si vrai, ne retourne que les enchères terminées dans la période spécifiée (si période spécifiée) ou toutes les enchères terminées si aucune période n'est spécifiée.") @RequestParam(name = "onlyEnded", defaultValue = "false") boolean onlyEnded,

			@Parameter(description = "Format du fichier exporté : 'json' (défaut) ou 'csv'.") @RequestParam(name = "format", defaultValue = "json") String format);
}