package be.labil.anacarde.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "transformer")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
/** Entité représentant un transformateur dans le système. */
public class Transformer {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transformer_seq")
	@SequenceGenerator(name = "transformer_seq", sequenceName = "transformer_seq", allocationSize = 1)
	private Integer id;

	@Column(name = "product_type", nullable = false)
	private String productType;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Seller_id", nullable = false)
	@ToString.Exclude
	private Seller seller;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "buyer_id", nullable = false)
	@ToString.Exclude
	private Buyer buyer;

}
