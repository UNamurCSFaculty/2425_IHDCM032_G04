package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "harvest_product")
@Getter
@Setter
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
