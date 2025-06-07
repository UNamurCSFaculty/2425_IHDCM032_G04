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
 * Interface REST pour les points d’accès d’administration du tableau de bord.
 * <p>
 * Définit deux opérations :
 * <ul>
 * <li>Récupération des KPI globaux (cartes) via la vue SQL <code>v_dashboard_cards</code>.</li>
 * <li>Récupération de la série chronologique "Open vs New" via la vue SQL
 * <code>v_dashboard_graphic</code>.</li>
 * </ul>
 * <p>
 * Toutes les requêtes sont sécurisées par JWT.
 */
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/admin/dashboard", produces = "application/json")
@Tag(name = "admin")
public interface AdminDashboardApi {

	/**
	 * Obtient les indicateurs globaux à afficher sous forme de cartes KPI.
	 * <p>
	 * Interroge la vue matérialisée <code>v_dashboard_cards</code> pour récupérer un objet
	 * {@link DashboardCardsDto} contenant les métriques agrégées (nombre total d’utilisateurs,
	 * ventes, etc.).
	 *
	 * @return {@link ResponseEntity} contenant le {@link DashboardCardsDto} et un code 200, ou un
	 *         code 404 avec un corps {@link ApiErrorResponse} si la vue est introuvable.
	 */
	@Operation(summary = "Obtenir la carte des indicateurs globaux")
	@GetMapping("/cards")
	@ApiResponses({
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = DashboardCardsDto.class))),
			@ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<DashboardCardsDto> getDashboardCards();

	/**
	 * Obtient la série chronologique "Open vs New" pour le dashboard.
	 * <p>
	 * Interroge la vue matérialisée <code>v_dashboard_graphic</code> pour récupérer une liste de
	 * {@link DashboardGraphicDto}, chacun représentant un point dans le temps avec les valeurs
	 * "ouvertes" et "nouvelles".
	 *
	 * @return {@link ResponseEntity} contenant la liste de {@link DashboardGraphicDto} et un code
	 *         200.
	 */
	@Operation(summary = "Obtenir la série chronologique « Open vs New »")
	@GetMapping("/graphic")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée", content = @Content(array = @ArraySchema(schema = @Schema(implementation = DashboardGraphicDto.class))))})
	ResponseEntity<List<DashboardGraphicDto>> getDashboardGraphicSeries();
}
