package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
	private Double price;

	@Column(nullable = false)
	private Integer productQuantity;

	@Column(nullable = false)
	private LocalDateTime expirationDate;

	@Column(nullable = false)
	@CreationTimestamp
	private LocalDateTime creationDate;

	@Column(nullable = false)
	private Boolean active;

	@Embedded
	private AuctionOptions options;

	@ManyToOne(optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@ManyToOne(optional = false)
	@JoinColumn(name = "trader_id", nullable = false)
	private Trader trader;

	@ManyToOne(optional = false)
	@JoinColumn(name = "status_id", nullable = false)
	private TradeStatus status;

	@OneToMany(mappedBy = "auctionId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Bid> bids;
}
