package be.labil.anacarde.domain.dto.db.product;

import be.labil.anacarde.domain.dto.db.BaseDto;
import be.labil.anacarde.domain.dto.db.StoreDetailDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO abstrait pour l'entité Product.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = HarvestProductDto.class, name = "harvest"),
		@JsonSubTypes.Type(value = TransformedProductDto.class, name = "transformed")})
@Schema(description = "Data Transfer Object pour un produit", requiredProperties = {
		"type"}, discriminatorProperty = "type", discriminatorMapping = {
				@DiscriminatorMapping(value = "harvest", schema = HarvestProductDto.class),
				@DiscriminatorMapping(value = "transformed", schema = TransformedProductDto.class)}, subTypes = {
						HarvestProductDto.class, TransformedProductDto.class})
public abstract class ProductDto extends BaseDto {

	@Column(nullable = false)
	private LocalDateTime deliveryDate;

	/** Magasin associé au produit récolté. */
	@NotNull(message = "Le magasin est requis")
	@Schema(description = "Magasin associé au produit récolté", requiredMode = Schema.RequiredMode.REQUIRED)
	private StoreDetailDto store;

	@Schema(description = "Poids en kg du produit", example = "100.0", requiredMode = Schema.RequiredMode.REQUIRED)
	private Double weightKg;

	@JsonIgnoreProperties("product")
	private Integer qualityControlId;

	/**
	 * Propriété virtuelle pour Swagger. Ce getter n'est pas utilisé par Jackson car il est ignoré,
	 * mais permet d'afficher dans le schéma OpenAPI une propriété "type" avec un exemple et des
	 * valeurs autorisées.
	 */
	@Schema(name = "type", description = "Type de produit.", requiredMode = Schema.RequiredMode.REQUIRED, example = "harvest", allowableValues = {
			"harvest", "transformed"})
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String getVirtualTypeForSwagger() {
		return null;
	}
}
