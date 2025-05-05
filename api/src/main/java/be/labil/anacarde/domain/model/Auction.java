package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "auction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Auction extends BaseEntity {

	@Column(nullable = false)
	private BigDecimal price;

	@Column(nullable = false)
	private Integer productQuantity;

	@Column(nullable = false)
	private LocalDateTime expirationDate;

	@Column(nullable = false)
	@CreationTimestamp
	private LocalDateTime creationDate;

	@Column(nullable = false)
	private Boolean active;

	@ManyToOne(optional = false)
	@JoinColumn(name = "strategy_id", nullable = false)
	private AuctionStrategy strategy;

	@ManyToOne(optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "auction_id")
	private Set<AuctionOptionValue> auctionOptionValues;

	@ManyToOne(optional = false)
	@JoinColumn(name = "trader_id", nullable = false)
	private Trader trader;

	@ManyToOne(optional = false)
	@JoinColumn(name = "status_id", nullable = false)
	private TradeStatus status;
}
