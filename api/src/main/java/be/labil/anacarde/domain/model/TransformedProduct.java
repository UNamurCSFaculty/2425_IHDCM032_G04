package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
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

	@Column(nullable = false)
	private String location;

	@ManyToOne
	private Transformer transformer;
}
