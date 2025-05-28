package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.user.UserDetailDto;
import be.labil.anacarde.domain.dto.db.user.UserListDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Validated
@Tag(name = "Users", description = "API de gestion des utilisateurs Administrateur")
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/admin/users", produces = "application/json")
public interface AdminUserController {

	/**
	 * Récupère les informations détaillées d'un utilisateur à partir de son identifiant.
	 *
	 * @param id
	 *            L'identifiant de l'utilisateur à récupérer.
	 * @return Les détails de l'utilisateur.
	 */
	@Operation(summary = "Récupérer les détails d'un utilisateur", description = "Renvoie les informations détaillées d'un utilisateur à partir de son ID.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Utilisateur trouvé", content = @Content(schema = @Schema(implementation = UserDetailDto.class))),
			@ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@GetMapping("/{id}")
	ResponseEntity<? extends UserDetailDto> getUser(
			@NotNull(message = "L'ID de l'utilisateur est obligatoire") @Positive(message = "L'ID doit être un entier positif") @Parameter(description = "Identifiant de l'utilisateur", example = "1", required = true) @PathVariable("id") Integer id);

	/**
	 * Renvoie la liste de tous les utilisateurs.
	 *
	 * @return La liste des utilisateurs.
	 */
	@Operation(summary = "Lister tous les utilisateurs", description = "Renvoie la liste de tous les utilisateurs présents dans le système.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserListDto.class))))})
	@GetMapping
	ResponseEntity<List<? extends UserListDto>> listUsers();
}
