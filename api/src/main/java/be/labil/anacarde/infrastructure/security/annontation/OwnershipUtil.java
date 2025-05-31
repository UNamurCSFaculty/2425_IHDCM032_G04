package be.labil.anacarde.infrastructure.security.annontation;

import be.labil.anacarde.infrastructure.persistence.AuctionRepository;
import be.labil.anacarde.infrastructure.persistence.BidRepository;
import be.labil.anacarde.infrastructure.persistence.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("ownership")
@RequiredArgsConstructor
public class OwnershipUtil {

	private final DocumentRepository docRepo;
	private final AuctionRepository auctionRepo;
	private final BidRepository bidRepo;

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
}
