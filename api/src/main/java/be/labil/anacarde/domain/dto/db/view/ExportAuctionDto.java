package be.labil.anacarde.domain.dto.db.view;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Représente UNE ligne de la vue v_auction_bid_analysis.
 */
public record ExportAuctionDto(

        /* ===== AUCTION ===== */
        Integer        auctionId,
        LocalDateTime  auctionStartDate,
        LocalDateTime  auctionEndDate,
        Double         auctionStartPrice,
        Boolean        auctionEnded,
        String         auctionStatus,

        /* options */
        String         strategyName,
        Double         optionMinPriceKg,
        Double         optionMaxPriceKg,
        Double         optionBuyNowPrice,
        Boolean        optionShowPublic,
        Integer        optionMinIncrement,

        /* ===== PRODUIT ===== */
        Integer        productId,
        Double         productWeightKg,
        LocalDateTime  productDepositDate,
        Integer        transformedProductId,

        /* quality control */
        Integer        qualityInspectorId,
        String         productQuality,
        String         productType,

        /* ===== STORE ===== */
        Integer        storeId,
        String         storeName,
        String         storeCity,
        String         storeRegion,

        /* ===== VENDEUR ===== */
        Integer        sellerId,
        String         sellerCity,
        String         sellerRegion,
        String         sellerCooperative,

        /* ===== AGRÉGATS BIDS ===== */
        Long           bidCount,
        BigDecimal     bidMax,
        BigDecimal     bidMin,
        BigDecimal     bidAvg,
        BigDecimal     bidSum,

        /* ===== WINNER ===== */
        Integer        winnerTraderId,
        BigDecimal     bidWinningAmount,
        String         winnerCity,
        String         winnerRegion
) {}