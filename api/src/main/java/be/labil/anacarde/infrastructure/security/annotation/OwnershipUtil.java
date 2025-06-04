package be.labil.anacarde.infrastructure.security.annotation;

import be.labil.anacarde.domain.dto.write.BidUpdateDto;
import be.labil.anacarde.domain.dto.write.product.HarvestProductUpdateDto;
import be.labil.anacarde.domain.dto.write.product.ProductUpdateDto;
import be.labil.anacarde.domain.dto.write.product.TransformedProductUpdateDto;
import be.labil.anacarde.domain.model.Bid;
import be.labil.anacarde.infrastructure.persistence.*;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("ownership")
@RequiredArgsConstructor
public class OwnershipUtil {

	private final DocumentRepository documentRepository;
	private final AuctionRepository auctionRepository;
	private final BidRepository bidRepository;
	private final HarvestProductRepository harvestProductRepository;
	private final TransformedProductRepository transformedProductRepository;
	private final StoreRepository storeRepository;

	public boolean isDocumentOwner(Integer docId, Integer userId) {
		return documentRepository.existsByIdAndUserId(docId, userId);
	}

	public boolean isAuctionOwner(Integer traderId, Integer auctionId) {
		if (traderId == null || auctionId == null) return false;
		return auctionRepository.existsByIdAndTraderId(auctionId, traderId);
	}

	public boolean isBidOwner(Integer traderId, Integer bidId) {
		if (traderId == null || bidId == null) return false;
		return bidRepository.existsByIdAndTraderId(bidId, traderId);
	}

	public boolean isBidAuctionOwner(Integer traderId, Integer bidId) {
		if (traderId == null || bidId == null) return false;

		Optional<Bid> bid = bidRepository.findById(bidId);
		if (!bid.isPresent()) return false;
		return auctionRepository.existsByIdAndTraderId(bid.get().getAuctionId(), traderId);
	}

	public boolean isBidAuthorized(Integer traderId, BidUpdateDto bidDto) {
		// safety checks
		if (traderId == null || bidDto == null) return false;

		// trader cannot bid in the name of someone else
		if (!traderId.equals(bidDto.getTraderId())) return false;

		// trader cannot bid on his own auction
		if (isAuctionOwner(traderId, bidDto.getTraderId())) return false;

		// trader cannot bid twice in a row
		List<Bid> bids = bidRepository.findByAuctionIdOrderByIdAsc(bidDto.getAuctionId());
		if (bids.size() > 0 && bids.getLast().getTrader().getId().equals(traderId)) return false;

		return true;
	}

	public boolean isProductOwner(Integer userId, Integer productId) {
		if (userId == null || productId == null) return false;
		return harvestProductRepository.existsByIdAndProducerId(productId, userId)
				|| transformedProductRepository.existsByIdAndTransformerId(productId, userId);
	}

	public boolean isProductOwner(Integer userId, ProductUpdateDto productDto) {
		if (userId == null || productDto == null) return false;

		if (productDto instanceof HarvestProductUpdateDto) {
			return userId.equals(((HarvestProductUpdateDto) productDto).getProducerId());
		}

		if (productDto instanceof TransformedProductUpdateDto) {
			return userId.equals(((TransformedProductUpdateDto) productDto).getTransformerId());
		}

		return false;
	}

	public boolean isStoreOwner(Integer userId, Integer storeId) {
		if (userId == null || storeId == null) return false;
		return storeRepository.existsByIdAndUserId(storeId, userId);
	}
}
