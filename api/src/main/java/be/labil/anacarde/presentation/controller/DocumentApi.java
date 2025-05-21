package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.DocumentDto;
import be.labil.anacarde.domain.dto.db.ValidationGroups;
import be.labil.anacarde.presentation.controller.annotations.ApiValidId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface REST pour les opérations sur les documents : - CRUD des méta-informations -
 * téléchargement du contenu binaire
 */
@Validated
@RequestMapping(value = "/api/documents", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "documents", description = "Opérations relatives aux documents")
public interface DocumentApi {

	/**
	 * Télécharge le contenu binaire brut du document identifié. Renvoie un flux octet-stream avec
	 * le bon Content-Type et Content-Disposition.
	 */
	@Operation(summary = "Télécharger le fichier brut d’un document")
	@ApiResponses({@ApiResponse(responseCode = "200", description = "Flux binaire renvoyé"),
			@ApiResponse(responseCode = "404", description = "Document non trouvé", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erreur lecture document", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@GetMapping(value = "/{id}/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	ResponseEntity<Resource> downloadDocument(@ApiValidId @PathVariable("id") Integer id);

	/**
	 * Récupère les méta-informations d’un document par son ID.
	 */
	@Operation(summary = "Obtenir un document")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Document trouvé", content = @Content(schema = @Schema(implementation = DocumentDto.class))),
			@ApiResponse(responseCode = "404", description = "Document non trouvé", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@GetMapping("/{id}")
	ResponseEntity<DocumentDto> getDocument(@ApiValidId @PathVariable("id") Integer id);

	/**
	 * Crée un document pour un utilisateur donné et téléverse son fichier.
	 */
	@Operation(summary = "Créer un document et téléverser un fichier")
	@RequestBody(required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "object", properties = {
			@StringToClassMapItem(key = "file", value = MultipartFile.class)}), encoding = @Encoding(name = "file", contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE)))
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Document créé", content = @Content(schema = @Schema(implementation = DocumentDto.class))),
			@ApiResponse(responseCode = "400", description = "Validation KO", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erreur stockage", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@PostMapping(path = "/users/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	ResponseEntity<DocumentDto> createDocument(@ApiValidId @PathVariable("userId") Integer userId,
			@Validated({Default.class,
					ValidationGroups.Create.class}) @RequestPart("file") MultipartFile file);

	/**
	 * Supprime un document (méta + fichier physique).
	 */
	@Operation(summary = "Supprimer un document")
	@ApiResponses({@ApiResponse(responseCode = "204", description = "Document supprimé"),
			@ApiResponse(responseCode = "404", description = "Document non trouvé", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	ResponseEntity<Void> deleteDocument(@ApiValidId @PathVariable("id") Integer id);

	/**
	 * Liste tous les documents d’un utilisateur.
	 */
	@Operation(summary = "Lister les documents d’un utilisateur")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @Schema(implementation = DocumentDto.class)))),
			@ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@GetMapping("/users/{userId}")
	ResponseEntity<List<DocumentDto>> listDocumentsByUser(
			@ApiValidId @PathVariable("userId") Integer userId);
}
