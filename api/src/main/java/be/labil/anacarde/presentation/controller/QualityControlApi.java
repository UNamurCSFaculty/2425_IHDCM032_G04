package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.dto.QualityControlDto;
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
@RequestMapping(value = "/api/products/{productId}/quality-controls", produces = "application/json")
@Tag(name = "quality-controls", description = "Opérations relatives aux contrôles qualité")
@Tag(name = "users", description = "Opérations relatives aux utilisateurs")
public interface QualityControlApi {

	@Operation(summary = "Obtenir un contrôle qualité")
	@GetMapping("/{id}")
	@ApiResponseGet
	ResponseEntity<? extends QualityControlDto> getQualityControl(@ApiValidId @PathVariable("id") Integer id);

	@Operation(summary = "Créer un contrôle qualité")
	@PostMapping
	@ApiResponsePost
	ResponseEntity<? extends QualityControlDto> createQualityControl(@Validated({Default.class,
			ValidationGroups.Create.class}) @RequestBody QualityControlDto qualityControlDto);

	@Operation(summary = "Mettre à jour un contrôle qualité")
	@PutMapping("/{id}")
	@ApiResponsePut
	ResponseEntity<? extends QualityControlDto> updateQualityControl(@ApiValidId @PathVariable("id") Integer id,
			@Validated({Default.class,
					ValidationGroups.Update.class}) @RequestBody QualityControlDto qualityControlDto);

	@Operation(summary = "Lister tous les contrôles qualité d’un produit")
	@GetMapping
	@ApiResponseGet
	ResponseEntity<List<? extends QualityControlDto>> listQualityControls(@PathVariable("productId") Integer productId);

	@Operation(summary = "Supprimer un contrôle qualité")
	@DeleteMapping("/{id}")
	@ApiResponseDelete
	@ResponseStatus(HttpStatus.NO_CONTENT)
	ResponseEntity<Void> deleteQualityControl(@ApiValidId @PathVariable("id") Integer id);
}
