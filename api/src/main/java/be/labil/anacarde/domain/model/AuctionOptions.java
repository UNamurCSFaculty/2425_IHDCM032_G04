package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AuctionOptions {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "strategy_id")
	private AuctionStrategy strategy;

	@Column(name = "fixed_price_kg")
	private Double fixedPriceKg;

	@Column(name = "max_price_kg")
	private Double maxPriceKg;

	@Column(name = "min_price_kg")
	private Double minPriceKg;

	@Column(name = "buy_now_price")
	private Double buyNowPrice;

	@Column(name = "show_public", nullable = false)
	@Builder.Default
	private Boolean showPublic = Boolean.TRUE;

	@Column(name = "force_better_bids", nullable = false)
	@Builder.Default
	private Boolean forceBetterBids = Boolean.FALSE;

	@Column(name = "min_increment", nullable = false)
	@Builder.Default
	private Integer minIncrement = 1;
}
