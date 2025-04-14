package be.labil.anacarde.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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

	@Column(nullable = false)
	private LocalDateTime deliveryDate;

	@ManyToOne(optional = false)
	private Producer producer;

	// TOOD set optional = false
	@ManyToOne(optional = true)
	private Field field;

	// TODO zak: pourquoi ça là et pas dans l'autre sens
	@ManyToOne
	private TransformedProduct transformedProduct;
}
