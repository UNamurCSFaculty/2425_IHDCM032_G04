package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.ValidationGroups;
import be.labil.anacarde.domain.dto.db.user.UserDetailDto;
import be.labil.anacarde.domain.dto.db.user.UserListDto;
import be.labil.anacarde.domain.dto.write.user.UserUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.groups.Default;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Cette API offre des points d'accès permettant de récupérer, créer, mettre à jour et supprimer des
 * utilisateurs.
 */
@Validated
@Tag(name = "Users", description = "API de gestion des utilisateurs")
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/users", produces = "application/json")
public interface UserApi {

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
	 * Crée un nouvel utilisateur (et, éventuellement, téléverse des documents associés).
	 * 
	 * @param user
	 *            Détails de l'utilisateur à créer.
	 * @param documents
	 *            Documents à téléverser
	 * @return L'utilisateur créé.
	 */
	@Operation(summary = "Créer un utilisateur et téléverser des documents")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "object", properties = {
			@StringToClassMapItem(key = "user", value = UserUpdateDto.class),
			@StringToClassMapItem(key = "documents", value = MultipartFile[].class)}), encoding = {
					@Encoding(name = "user", contentType = MediaType.APPLICATION_JSON_VALUE),
					@Encoding(name = "documents", contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE)}))
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès", content = @Content(schema = @Schema(implementation = UserDetailDto.class, discriminatorProperty = "type"))),
			@ApiResponse(responseCode = "400", description = "Erreur de validation ou JSON invalide", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "Conflit avec un utilisateur existant", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<? extends UserDetailDto> createUser(
			@Validated({Default.class,
					ValidationGroups.Create.class}) @RequestPart("user") @Valid UserUpdateDto user,
			@RequestPart(value = "documents", required = false) List<MultipartFile> documents);

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

	/**
	 * Met à jour un utilisateur existant.
	 * 
	 * @param id
	 *            L'identifiant de l'utilisateur à mettre à jour.
	 * @param userUpdateDto
	 *            Détails de l'utilisateur à mettre à jour.
	 * @return L'utilisateur mis à jour.
	 */
	@Operation(summary = "Mettre à jour un utilisateur", description = "Met à jour un utilisateur existant en utilisant l'ID spécifié.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Utilisateur mis à jour avec succès", content = @Content(schema = @Schema(implementation = UserDetailDto.class))),
			@ApiResponse(responseCode = "400", description = "Erreur de validation ou JSON invalide", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<? extends UserDetailDto> updateUser(
			@NotNull(message = "L'ID de l'utilisateur est obligatoire") @Positive(message = "L'ID doit être un entier positif") @Parameter(description = "Identifiant de l'utilisateur", example = "1", required = true) @PathVariable("id") Integer id,
			@Validated({Default.class,
					ValidationGroups.Update.class}) @RequestBody UserUpdateDto userUpdateDto);

	/**
	 * Supprime un utilisateur.
	 * 
	 * @param id
	 *            L'identifiant de l'utilisateur à supprimer.
	 */
	@Operation(summary = "Supprimer un utilisateur", description = "Supprime un utilisateur en fonction de son identifiant.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Utilisateur supprimé avec succès"),
			@ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	ResponseEntity<Void> deleteUser(
			@NotNull(message = "L'ID de l'utilisateur est obligatoire") @Positive(message = "L'ID doit être un entier positif") @Parameter(description = "Identifiant de l'utilisateur", example = "1", required = true) @PathVariable("id") Integer id);

	/**
	 * Ajoute un rôle à un utilisateur.
	 * 
	 * @param id
	 *            L'identifiant de l'utilisateur.
	 * @param roleName
	 *            Le nom du rôle à ajouter.
	 * @return L'utilisateur misà jour.
	 */
	@Operation(summary = "Ajouter un rôle à un utilisateur", description = "Ajoute un rôle spécifique à l'utilisateur.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Rôle ajouté avec succès", content = @Content(schema = @Schema(implementation = UserDetailDto.class))),
			@ApiResponse(responseCode = "400", description = "Erreur de validation ou JSON invalide", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Utilisateur ou rôle non trouvé", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@PostMapping("/{id}/roles/{roleName}")
	ResponseEntity<? extends UserDetailDto> addRoleToUser(
			@NotNull(message = "L'ID de l'utilisateur est obligatoire") @Positive(message = "L'ID doit être un entier positif") @Parameter(description = "Identifiant de l'utilisateur", example = "1", required = true) @PathVariable("id") Integer id,
			@NotNull(message = "Le nom du rôle est obligatoire") @Parameter(description = "Nom du rôle", example = "ROLE_USER", required = true) @PathVariable("roleName") String roleName);

	/**
	 * Remplace complètement les rôles d'un utilisateur.
	 * 
	 * @param id
	 *            L'identifiant de l'utilisateur.
	 * @param roleNames
	 *            Liste des noms de rôles à attribuer.
	 * @return L'utilisateur mis à jour.
	 */
	@Operation(summary = "Mettre à jour les rôles d'un utilisateur", description = "Remplace l'ensemble des rôles de l'utilisateur spécifié.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Rôles mis à jour avec succès", content = @Content(schema = @Schema(implementation = UserDetailDto.class))),
			@ApiResponse(responseCode = "400", description = "Erreur de validation ou JSON invalide", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Utilisateur ou rôle non trouvé", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@PutMapping(value = "/{id}/roles", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<? extends UserDetailDto> updateUserRoles(
			@NotNull(message = "L'ID de l'utilisateur est obligatoire") @Positive(message = "L'ID doit être un entier positif") @Parameter(description = "Identifiant de l'utilisateur", example = "1", required = true) @PathVariable("id") Integer id,
			@NotNull(message = "La liste des rôles est obligatoire") @RequestBody List<String> roleNames);
}
