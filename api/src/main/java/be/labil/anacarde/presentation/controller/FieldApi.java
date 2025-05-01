package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.dto.FieldDto;
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
@RequestMapping(value = "/api/users/{userId}/fields", produces = "application/json")
@Tag(name = "fields", description = "Opérations relatives aux champs")
@Tag(name = "users", description = "Opérations relatives aux utilisateurs")
public interface FieldApi {

	@Operation(summary = "Obtenir un champ")
	@GetMapping("/{id}")
	@ApiResponseGet
	ResponseEntity<FieldDto> getField(@ApiValidId @PathVariable("id") Integer id);

	@Operation(summary = "Créer un champ")
	@PostMapping
	@ApiResponsePost
	ResponseEntity<FieldDto> createField(
			@Validated({Default.class, ValidationGroups.Create.class}) @RequestBody FieldDto fieldDto);

	@Operation(summary = "Mettre à jour un champ")
	@PutMapping(value = "/{id}", consumes = "application/json")
	@ApiResponsePut
	ResponseEntity<FieldDto> updateField(@ApiValidId @PathVariable("id") Integer id,
			@Validated({Default.class, ValidationGroups.Update.class}) @RequestBody FieldDto fieldDto);

	@Operation(summary = "Obtenir tous les champs d’un utilisateur")
	@GetMapping
	@ApiResponseGet
	ResponseEntity<List<FieldDto>> listFields(@PathVariable("userId") Integer userId);

	@Operation(summary = "Supprimer un champ")
	@DeleteMapping("/{id}")
	@ApiResponseDelete
	@ResponseStatus(HttpStatus.NO_CONTENT)
	ResponseEntity<Void> deleteField(@ApiValidId @PathVariable("id") Integer id);
}
