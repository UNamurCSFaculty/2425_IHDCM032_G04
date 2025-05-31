package be.labil.anacarde.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Entité représentant une offre de contrat faite entre deux Trader (un acheteur et un vendeur).
 */
@Entity
@Table(name = "contract_offer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ContractOffer extends BaseEntity {

	@Column(nullable = false)
	private String status;

	@Column(nullable = false)
	private BigDecimal pricePerKg;

	@Column(nullable = false)
	@CreationTimestamp
	private LocalDateTime creationDate;

	@Column(nullable = false)
	private LocalDateTime endDate;

	@ManyToOne(optional = false)
	private Trader seller;

	@ManyToOne(optional = false)
	private Trader buyer;

	@ManyToOne(optional = false)
	private Quality quality;
}