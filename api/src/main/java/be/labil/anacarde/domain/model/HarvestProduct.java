package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Entité représentant un produit cultivé.
 */
@Entity
@Table(name = "harvest_product")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class HarvestProduct extends Product {

	@ManyToOne(optional = false)
	@JoinColumn(name = "producer_id", nullable = false)
	private Producer producer;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "field_id")
	private Field field;

	@ManyToOne
	@JoinColumn(name = "transformed_product_id")
	private TransformedProduct transformedProduct;
}
