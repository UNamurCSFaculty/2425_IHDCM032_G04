package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "transformed_product")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class TransformedProduct extends Product {

	@Column(nullable = false)
	private String identifier;

	@ManyToOne(optional = false)
	@JoinColumn(name = "transformer_id", nullable = false)
	private Transformer transformer;

	// TODO EAGER
	@OneToMany(mappedBy = "transformedProduct", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<HarvestProduct> harvestProducts;
}
