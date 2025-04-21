package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.dto.DocumentDto;
import be.labil.anacarde.domain.dto.ValidationGroups;
import be.labil.anacarde.presentation.controller.annotations.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/documents", produces = "application/json")
@Tag(name = "documents", description = "Opérations relatives aux documents")
public interface DocumentApi {

	@Operation(summary = "Obtenir un document")
	@GetMapping("/{id}")
	@ApiResponseGet
	ResponseEntity<? extends DocumentDto> getDocument(@ApiValidId @PathVariable("id") Integer id);

	@Operation(summary = "Créer un document")
	@PostMapping
	@ApiResponsePost
	ResponseEntity<? extends DocumentDto> createDocument(
			@Validated({Default.class, ValidationGroups.Create.class}) @RequestBody DocumentDto documentDto);

	@Operation(summary = "Mettre à jour un document")
	@PutMapping("/{id}")
	@ApiResponsePut
	ResponseEntity<? extends DocumentDto> updateDocument(@ApiValidId @PathVariable("id") Integer id,
			@Validated({Default.class, ValidationGroups.Update.class}) @RequestBody DocumentDto documentDto);

	@Operation(summary = "Supprimer un document")
	@DeleteMapping("/{id}")
	@ApiResponseDelete
	@ResponseStatus(HttpStatus.NO_CONTENT)
	ResponseEntity<Void> deleteDocument(@ApiValidId @PathVariable("id") Integer id);

	// @Operation(summary = "Lister les documents par contrôle qualité")
	// @GetMapping("/quality-controls/{qualityControlId}")
	// @ApiResponseGet
	// ResponseEntity<List<? extends DocumentDto>> listDocumentsByQualityControl(@PathVariable Integer
	// qualityControlId);

	@Operation(summary = "Lister les documents par utilisateur")
	@GetMapping("/users/{userId}")
	@ApiResponseGet
	ResponseEntity<List<? extends DocumentDto>> listDocumentsByUser(@PathVariable Integer userId);
}
