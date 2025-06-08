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
 * API REST pour la gestion des documents.
 * <p>
 * Permet de :
 * <ul>
 * <li>Télécharger le contenu binaire d’un document.</li>
 * <li>Consulter les méta-informations d’un document.</li>
 * <li>Créer un document lié à un utilisateur ou à un contrôle qualité.</li>
 * <li>Supprimer un document (métadonnées et contenu physique).</li>
 * <li>Lister tous les documents d’un utilisateur.</li>
 * </ul>
 */
@Validated
@RequestMapping(value = "/api/documents", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "documents", description = "Gestion des documents")
public interface DocumentApi {

	/**
	 * Télécharge le contenu binaire brut du document identifié.
	 * <p>
	 * Renvoie un flux {@code application/octet-stream} avec les en-têtes {@code Content-Type} et
	 * {@code Content-Disposition} appropriés.
	 *
	 * @param id
	 *            Identifiant du document (doit être un entier positif et non null)
	 * @return {@code 200 OK} avec le flux binaire en corps de réponse, {@code 404 Not Found} si le
	 *         document n’existe pas, {@code 500 Internal Server Error} en cas d’erreur de lecture.
	 */
	@Operation(summary = "Télécharger le fichier brut d’un document")
	@ApiResponses({@ApiResponse(responseCode = "200", description = "Flux binaire renvoyé"),
			@ApiResponse(responseCode = "404", description = "Document non trouvé", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erreur lecture document", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@GetMapping(value = "/{id}/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	ResponseEntity<Resource> downloadDocument(@ApiValidId @PathVariable("id") Integer id);

	/**
	 * Récupère les méta-informations d’un document par son ID.
	 *
	 * @param id
	 *            Identifiant du document (positif, non null)
	 * @return {@code 200 OK} avec un {@link DocumentDto} en JSON, {@code 404 Not Found} si le
	 *         document n’existe pas.
	 */
	@Operation(summary = "Obtenir un document")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Document trouvé", content = @Content(schema = @Schema(implementation = DocumentDto.class))),
			@ApiResponse(responseCode = "404", description = "Document non trouvé", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@GetMapping("/{id}")
	ResponseEntity<DocumentDto> getDocument(@ApiValidId @PathVariable("id") Integer id);

	/**
	 * Crée un document et téléverse son fichier pour un utilisateur donné.
	 * <p>
	 * La requête doit être un {@code multipart/form-data} contenant le champ {@code file} avec le
	 * contenu du document.
	 *
	 * @param userId
	 *            Identifiant de l’utilisateur propriétaire du document
	 * @param file
	 *            Fichier à stocker, validé selon {@link ValidationGroups.Create}
	 * @return {@code 201 Created} avec le {@link DocumentDto} créé, {@code 400 Bad Request} en cas
	 *         de validation KO, {@code 404 Not Found} si l’utilisateur n’existe pas,
	 *         {@code 500 Internal Server Error} en cas d’erreur de stockage.
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
	ResponseEntity<DocumentDto> createDocumentUser(
			@ApiValidId @PathVariable("userId") Integer userId, @Validated({Default.class,
					ValidationGroups.Create.class}) @RequestPart("file") MultipartFile file);

	/**
	 * Crée un document et téléverse son fichier pour un contrôle qualité donné.
	 *
	 * @param qualityControlId
	 *            Identifiant du contrôle qualité
	 * @param file
	 *            Fichier à stocker, validé selon {@link ValidationGroups.Create}
	 * @return {@code 201 Created} avec le {@link DocumentDto} créé, {@code 400 Bad Request} en cas
	 *         de validation KO, {@code 404 Not Found} si le contrôle qualité n’existe pas,
	 *         {@code 500 Internal Server Error} en cas d’erreur de stockage.
	 */
	@Operation(summary = "Créer un document et téléverser un fichier")
	@RequestBody(required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "object", properties = {
			@StringToClassMapItem(key = "file", value = MultipartFile.class)}), encoding = @Encoding(name = "file", contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE)))
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Document créé", content = @Content(schema = @Schema(implementation = DocumentDto.class))),
			@ApiResponse(responseCode = "400", description = "Validation KO", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Contrôle qualité non trouvé", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erreur stockage", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@PostMapping(path = "/quality-controls/{qualityControlId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	ResponseEntity<DocumentDto> createDocumentQualityControl(
			@ApiValidId @PathVariable("qualityControlId") Integer qualityControlId,
			@Validated({Default.class,
					ValidationGroups.Create.class}) @RequestPart("file") MultipartFile file);

	/**
	 * Supprime un document, à la fois ses méta-informations et son fichier physique.
	 *
	 * @param id
	 *            Identifiant du document à supprimer
	 * @return {@code 204 No Content} si la suppression réussit, {@code 404 Not Found} si le
	 *         document n’existe pas.
	 */
	@Operation(summary = "Supprimer un document")
	@ApiResponses({@ApiResponse(responseCode = "204", description = "Document supprimé"),
			@ApiResponse(responseCode = "404", description = "Document non trouvé", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	ResponseEntity<Void> deleteDocument(@ApiValidId @PathVariable("id") Integer id);

	/**
	 * Liste tous les documents d’un utilisateur spécifié.
	 *
	 * @param userId
	 *            Identifiant de l’utilisateur
	 * @return {@code 200 OK} avec la liste de {@link DocumentDto}, ou {@code 404 Not Found} si
	 *         l’utilisateur n’existe pas.
	 */
	@Operation(summary = "Lister les documents d’un utilisateur")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @Schema(implementation = DocumentDto.class)))),
			@ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@GetMapping("/users/{userId}")
	ResponseEntity<List<DocumentDto>> listDocumentsByUser(
			@ApiValidId @PathVariable("userId") Integer userId);
}
