package be.labil.anacarde.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO abstrait pour l'entité Product.
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = HarvestProductDto.class, name = "harvest"),
		@JsonSubTypes.Type(value = TransformedProductDto.class, name = "transformed")})
@Schema(description = "Data Transfer Object pour un produit", requiredProperties = {
		"type"}, discriminatorProperty = "type", discriminatorMapping = {
				@DiscriminatorMapping(value = "harvest", schema = HarvestProductDto.class),
				@DiscriminatorMapping(value = "transformed", schema = TransformedProductDto.class)})
public abstract class ProductDto {

	/** Identifiant unique du produit. */
	@Schema(description = "Identifiant unique du produit", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	@Schema(description = "Poids en kg du produit", example = "100.0")
	private Double weightKg;

	private QualityControlDto qualityControl;

	/**
	 * Propriété virtuelle pour Swagger. Ce getter n'est pas utilisé par Jackson car il est ignoré, mais permet
	 * d'afficher dans le schéma OpenAPI une propriété "type" avec un exemple et des valeurs autorisées.
	 */
	@Schema(name = "type", description = "Type de produit.", requiredMode = Schema.RequiredMode.REQUIRED, example = "harvest", allowableValues = {
			"harvest", "transformed"})
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String getVirtualTypeForSwagger() {
		return null;
	}
}
