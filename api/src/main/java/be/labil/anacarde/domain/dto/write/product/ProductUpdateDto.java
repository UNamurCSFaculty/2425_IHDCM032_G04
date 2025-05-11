package be.labil.anacarde.domain.dto.write.product;

import be.labil.anacarde.domain.dto.db.BaseDto;
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
@JsonSubTypes({@JsonSubTypes.Type(value = HarvestProductUpdateDto.class, name = "harvest"),
		@JsonSubTypes.Type(value = TransformedProductUpdateDto.class, name = "transformed")})
@Schema(description = "Objet de transfert pour créer ou mettre à jour un produit.", requiredProperties = {
		"type"}, discriminatorProperty = "type", discriminatorMapping = {
				@DiscriminatorMapping(value = "harvest", schema = HarvestProductUpdateDto.class),
				@DiscriminatorMapping(value = "transformed", schema = TransformedProductUpdateDto.class)}, subTypes = {
						HarvestProductUpdateDto.class, TransformedProductUpdateDto.class})
public abstract class ProductUpdateDto extends BaseDto {

	@Column(nullable = false)
	private LocalDateTime deliveryDate;

	/** Magasin associé au produit récolté. */
	@NotNull(message = "Le magasin est requis")
	@Schema(description = "Magasin associé au produit", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer storeId;

	@Schema(description = "Poids en kg du produit", example = "100.0")
	private Double weightKg;

	@NotNull(message = "Le contrôle qualité est requis")
	@Schema(description = "Contrôle qualité associé au produit", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer qualityControlId;
}
