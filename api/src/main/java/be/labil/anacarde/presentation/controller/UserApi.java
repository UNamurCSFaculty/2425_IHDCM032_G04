package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.ValidationGroups;
import be.labil.anacarde.domain.dto.db.user.UserDetailDto;
import be.labil.anacarde.domain.dto.write.user.create.UserCreateDto;
import be.labil.anacarde.domain.dto.write.user.update.UserUpdateDto;
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

	@Operation(summary = "Vérifier la disponibilité d’un e-mail", description = "204 si libre, 409 s’il existe déjà")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Résultat de la vérification", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(type = "boolean")))})
	@GetMapping("/check/email")
	ResponseEntity<Boolean> checkEmail(@RequestParam("email") @NotNull String email);

	@Operation(summary = "Vérifier la disponibilité d’un numéro de téléphone", description = "Renvoie `true` si le numéro existe déjà, `false` sinon.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Résultat de la vérification", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(type = "boolean")))})
	@GetMapping("/check/phone")
	ResponseEntity<Boolean> checkPhone(
			@Parameter(description = "Téléphone à tester", required = true) @RequestParam("phone") String phone);

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
			@StringToClassMapItem(key = "user", value = UserCreateDto.class),
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
					ValidationGroups.Create.class}) @RequestPart("user") @Valid UserCreateDto user,
			@RequestPart(value = "documents", required = false) List<MultipartFile> documents);

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
}
