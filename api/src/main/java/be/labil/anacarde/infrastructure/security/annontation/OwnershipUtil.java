package be.labil.anacarde.infrastructure.security.annontation;

import be.labil.anacarde.domain.dto.write.product.HarvestProductUpdateDto;
import be.labil.anacarde.domain.dto.write.product.ProductUpdateDto;
import be.labil.anacarde.domain.dto.write.product.TransformedProductUpdateDto;
import be.labil.anacarde.infrastructure.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("ownership")
@RequiredArgsConstructor
public class OwnershipUtil {

	private final DocumentRepository docRepo;
	private final AuctionRepository auctionRepo;
	private final BidRepository bidRepo;
	private final HarvestProductRepository harvestProductRepo;
	private final TransformedProductRepository transformedProductRepo;

	/**
	 * @return true si le document appartient à l’utilisateur.
	 */
	public boolean isDocumentOwner(Integer docId, Integer userId) {
		return docRepo.existsByIdAndUserId(docId, userId);
	}

	/**
	 * @return true si l'auction appartient à l'utilisateur.
	 */
	public boolean isAuctionOwner(Integer traderId, Integer auctionId) {
		if (traderId == null || auctionId == null) return false;
		return auctionRepo.existsByIdAndTraderId(auctionId, traderId);
	}

	/**
	 * @return true si l'auction appartient à l'utilisateur.
	 */
	public boolean isBidOwner(Integer traderId, Integer bidId) {
		if (traderId == null || bidId == null) return false;
		return bidRepo.existsByIdAndTraderId(bidId, traderId);
	}

	public boolean isProductOwner(Integer userId, Integer productId) {
		if (userId == null || productId == null) return false;
		return harvestProductRepo.existsByIdAndProducerId(productId, userId)
				|| transformedProductRepo.existsByIdAndTransformerId(productId, userId);
	}

	public boolean isProductOwner(Integer userId, ProductUpdateDto productDto) {
		if (userId == null || productDto == null) return false;

		if (productDto instanceof HarvestProductUpdateDto) {
			return userId == ((HarvestProductUpdateDto) productDto).getProducerId();
		}

		if (productDto instanceof TransformedProductUpdateDto) {
			return userId == ((TransformedProductUpdateDto) productDto).getTransformerId();
		}

		return false;
	}
}
