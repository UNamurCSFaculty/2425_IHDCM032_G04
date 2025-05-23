package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.Immutable;

/**
 * Représentation JPA de la vue SQL v_auction_bid_analysis. ⚠️ ENTITÉ EN LECTURE SEULE : pas
 * d’UPDATE/INSERT.
 */
@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Immutable
public class ExportAuction {

	/* ===== AUCTION ===== */
	@Id
	@Column(name = "auction_id")
	private Integer auctionId;

	@Column(name = "auction_start_date")
	private LocalDateTime auctionStartDate;

	@Column(name = "auction_end_date")
	private LocalDateTime auctionEndDate;

	@Column(name = "auction_start_price")
	private Double auctionStartPrice;

	@Column(name = "auction_ended")
	private Boolean auctionEnded;

	@Column(name = "auction_status")
	private String auctionStatus;

	/* ----- options ----- */
	@Column(name = "strategy_name")
	private String strategyName;

	@Column(name = "option_min_price_kg")
	private Double optionMinPriceKg;

	@Column(name = "option_max_price_kg")
	private Double optionMaxPriceKg;

	@Column(name = "option_buy_now_price")
	private Double optionBuyNowPrice;

	@Column(name = "option_show_public")
	private Boolean optionShowPublic;

	@Column(name = "option_min_increment")
	private Integer optionMinIncrement;

	/* ===== PRODUIT ===== */
	@Column(name = "product_id")
	private Integer productId;

	@Column(name = "product_weight_kg")
	private Double productWeightKg;

	@Column(name = "product_deposit_date")
	private LocalDateTime productDepositDate;

	@Column(name = "transformed_product_id")
	private Integer transformedProductId;

	/* quality control */
	@Column(name = "quality_inspector_id")
	private Integer qualityInspectorId;

	@Column(name = "product_quality")
	private String productQuality;

	@Column(name = "product_type")
	private String productType;

	/* ===== STORE ===== */
	@Column(name = "store_id")
	private Integer storeId;

	@Column(name = "store_name")
	private String storeName;

	@Column(name = "store_city")
	private String storeCity;

	@Column(name = "store_region")
	private String storeRegion;

	/* ===== VENDEUR ===== */
	@Column(name = "seller_id")
	private Integer sellerId;

	@Column(name = "seller_city")
	private String sellerCity;

	@Column(name = "seller_region")
	private String sellerRegion;

	@Column(name = "seller_cooperative")
	private String sellerCooperative;

	/* ===== AGRÉGATS BIDS ===== */
	@Column(name = "bid_count")
	private Long bidCount;

	@Column(name = "bid_max")
	private BigDecimal bidMax;

	@Column(name = "bid_min")
	private BigDecimal bidMin;

	@Column(name = "bid_avg")
	private BigDecimal bidAvg;

	@Column(name = "bid_sum")
	private BigDecimal bidSum;

	/* ===== WINNER ===== */
	@Column(name = "winner_trader_id")
	private Integer winnerTraderId;

	@Column(name = "bid_winning_amount")
	private BigDecimal bidWinningAmount;

	@Column(name = "winner_city")
	private String winnerCity;

	@Column(name = "winner_region")
	private String winnerRegion;
}
