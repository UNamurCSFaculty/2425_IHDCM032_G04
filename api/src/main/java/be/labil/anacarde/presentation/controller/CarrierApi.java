package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.dto.CarrierDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Ce contrôleur fournit des points d'accès pour récupérer, créer, mettre à jour et supprimer des transporteurs.
 */
@Tag(name = "Carriers", description = "API de gestion des transporteurs")
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/carriers", produces = "application/json")
public interface CarrierApi {

	/**
	 * Récupère les informations détaillées d'un transporteur à partir de son identifiant.
	 *
	 * @param id
	 *            L'identifiant du transporteur.
	 * @return Une ResponseEntity contenant les détails du transporteur.
	 */
	@Operation(summary = "Récupérer les détails d'un transporteur", description = "Renvoie les informations détaillées d'un transporteur à partir de son ID.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Transporteur trouvé", content = @Content(schema = @Schema(implementation = CarrierDto.class))),
			@ApiResponse(responseCode = "404", description = "Transporteur non trouvé", content = @Content(schema = @Schema(example = "{\"error\": \"Transporteur non trouvé\"}")))})
	@GetMapping("/{id}")
	ResponseEntity<CarrierDto> getCarrier(
			@NotNull(message = "L'ID du transporteur est obligatoire") @Positive(message = "L'ID doit être un entier positif") @Parameter(description = "Identifiant du transporteur", example = "1", required = true) @PathVariable("id") Integer id);

	/**
	 * Crée un nouveau transporteur en utilisant les données fournies dans CarrierDto.
	 *
	 * @param carrierDto
	 *            Les données du transporteur à créer.
	 * @return Une ResponseEntity contenant les détails du transporteur créé.
	 */
	@Operation(summary = "Créer un transporteur", description = "Crée un nouveau transporteur dans le système.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Transporteur créé avec succès", content = @Content(schema = @Schema(implementation = CarrierDto.class))),
			@ApiResponse(responseCode = "400", description = "Erreur de validation ou JSON invalide", content = @Content(schema = @Schema(example = "{\"error\": \"Données invalides\"}")))})
	@PostMapping(consumes = "application/json")
	ResponseEntity<CarrierDto> createCarrier(@RequestBody CarrierDto carrierDto);

	/**
	 * Récupère la liste de tous les transporteurs.
	 *
	 * @return Une ResponseEntity contenant la liste de tous les transporteurs.
	 */
	@Operation(summary = "Lister tous les transporteurs", description = "Renvoie la liste de tous les transporteurs présents dans le système.")
	@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
	@GetMapping
	ResponseEntity<List<CarrierDto>> listCarriers();

	/**
	 * Met à jour un transporteur existant en utilisant les données fournies dans CarrierDto.
	 *
	 * @param id
	 *            L'identifiant du transporteur à mettre à jour.
	 * @param carrierDto
	 *            Les nouvelles données du transporteur.
	 * @return Une ResponseEntity contenant les détails du transporteur mis à jour.
	 */
	@Operation(summary = "Mettre à jour un transporteur", description = "Met à jour un transporteur existant en utilisant l'ID spécifié dans l'URL.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Transporteur mis à jour avec succès", content = @Content(schema = @Schema(implementation = CarrierDto.class))),
			@ApiResponse(responseCode = "400", description = "Erreur de validation ou JSON invalide", content = @Content(schema = @Schema(example = "{\"error\": \"Données invalides\"}"))),
			@ApiResponse(responseCode = "404", description = "Transporteur non trouvé", content = @Content(schema = @Schema(example = "{\"error\": \"Transporteur non trouvé\"}")))})
	@PutMapping(value = "/{id}", consumes = "application/json")
	ResponseEntity<CarrierDto> updateCarrier(
			@NotNull(message = "L'ID du transporteur est obligatoire") @Positive(message = "L'ID doit être un entier positif") @Parameter(description = "Identifiant du transporteur", example = "1", required = true) @PathVariable("id") Integer id,
			@RequestBody CarrierDto carrierDto);

	/**
	 * Supprime un transporteur à partir de son identifiant.
	 *
	 * @param id
	 *            L'identifiant du transporteur à supprimer.
	 */
	@Operation(summary = "Supprimer un transporteur", description = "Supprime un transporteur en fonction de son identifiant.")
	@ApiResponses({@ApiResponse(responseCode = "204", description = "Transporteur supprimé avec succès"),
			@ApiResponse(responseCode = "404", description = "Transporteur non trouvé", content = @Content(schema = @Schema(example = "{\"error\": \"Transporteur non trouvé\"}")))})
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	ResponseEntity<Void> deleteCarrier(
			@NotNull(message = "L'ID du transporteur est obligatoire") @Positive(message = "L'ID doit être un entier positif") @Parameter(description = "Identifiant du transporteur", example = "1", required = true) @PathVariable("id") Integer id);
}
