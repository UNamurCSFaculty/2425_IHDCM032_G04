package be.labil.anacarde.domain.dto.db.view;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

/**
 * Ligne de la vue v_auction_bid_analysis. Immuable
 */
@Getter
@Builder
@AllArgsConstructor
public class ExportAuctionDto {

	/* ===== AUCTION ===== */
	private Integer auctionId;
	private LocalDateTime auctionStartDate;
	private LocalDateTime auctionEndDate;
	private Double auctionStartPrice;
	private Boolean auctionEnded;
	private String auctionStatus;

	/* options */
	private String strategyName;
	private Double optionMinPriceKg;
	private Double optionMaxPriceKg;
	private Double optionBuyNowPrice;
	private Boolean optionShowPublic;
	private Integer optionMinIncrement;

	/* ===== PRODUIT ===== */
	private Integer productId;
	private Double productWeightKg;
	private LocalDateTime productDepositDate;
	private Integer transformedProductId;

	/* quality control */
	private Integer qualityInspectorId;
	private String productQuality;
	private String productType;

	/* ===== STORE ===== */
	private Integer storeId;
	private String storeName;
	private String storeCity;
	private String storeRegion;

	/* ===== VENDEUR ===== */
	private Integer sellerId;
	private String sellerCity;
	private String sellerRegion;
	private String sellerCooperative;

	/* ===== AGRÉGATS BIDS ===== */
	private Long bidCount;
	private BigDecimal bidMax;
	private BigDecimal bidMin;
	private BigDecimal bidAvg;
	private BigDecimal bidSum;

	/* ===== WINNER ===== */
	private Integer winnerTraderId;
	private BigDecimal bidWinningAmount;
	private String winnerCity;
	private String winnerRegion;
}
