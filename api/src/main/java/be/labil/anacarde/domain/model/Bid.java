package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Entité représentant une offre d'achat sur une offre de vente.
 */
@Entity
@Table(name = "bid")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Bid extends BaseEntity {

	@Column(nullable = false)
	private BigDecimal amount;

	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime creationDate;

	@Column(name = "auction_id", nullable = false)
	private Integer auctionId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "trader_id", nullable = false)
	private Trader trader;

	@ManyToOne(optional = false)
	@JoinColumn(name = "status_id", nullable = false)
	private TradeStatus status;
}
