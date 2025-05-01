package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.dto.QualityDto;
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
@RequestMapping(value = "/api/qualities", produces = "application/json")
@Tag(name = "quality", description = "Opérations relatives aux qualités")
public interface QualityApi {

	@Operation(summary = "Obtenir une qualité")
	@GetMapping("/{id}")
	@ApiResponseGet
	ResponseEntity<QualityDto> getQuality(@ApiValidId @PathVariable("id") Integer id);

	@Operation(summary = "Créer une qualité")
	@PostMapping
	@ApiResponsePost
	ResponseEntity<QualityDto> createQuality(
			@Validated({Default.class, ValidationGroups.Create.class}) @RequestBody QualityDto qualityDto);

	@Operation(summary = "Mettre à jour une qualité")
	@ApiResponsePut
	@PutMapping(value = "/{id}", consumes = "application/json")
	ResponseEntity<QualityDto> updateQuality(@ApiValidId @PathVariable("id") Integer id,
			@Validated({Default.class, ValidationGroups.Update.class}) @RequestBody QualityDto qualityDto);

	@Operation(summary = "Obtenir toutes les qualités")
	@ApiResponseGet
	@GetMapping
	ResponseEntity<List<QualityDto>> listQualities();

	@Operation(summary = "Supprimer une qualité")
	@ApiResponseDelete
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	ResponseEntity<Void> deleteQuality(@ApiValidId @PathVariable("id") Integer id);
}
