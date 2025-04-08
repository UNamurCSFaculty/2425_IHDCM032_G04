package be.labil.anacarde.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "harvest_product")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class HarvestProduct extends Product {

	@ManyToOne(optional = false)
	private Store store;

	@ManyToOne(optional = false)
	private Producer producer;

	@ManyToOne(optional = false)
	private Field field;

	@ManyToOne
	private TransformedProduct transformedProduct;
}
