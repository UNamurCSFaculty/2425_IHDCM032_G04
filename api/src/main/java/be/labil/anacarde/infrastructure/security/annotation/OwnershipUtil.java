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

/**
 * Utilitaire d’autorisation basé sur la propriété des entités.
 * <p>
 * Exposé sous le nom SpEL {@code ownership} pour vérifier si l’utilisateur courant
 * est bien le propriétaire d’un document, d’une enchère, d’une offre, d’un produit ou d’un magasin.
 */
@Component("ownership")
@RequiredArgsConstructor
public class OwnershipUtil {

	private final DocumentRepository documentRepository;
	private final AuctionRepository auctionRepository;
	private final BidRepository bidRepository;
	private final HarvestProductRepository harvestProductRepository;
	private final TransformedProductRepository transformedProductRepository;
	private final StoreRepository storeRepository;

	/**
	 * Vérifie qu’un document appartient à un utilisateur.
	 *
	 * @param docId  identifiant du document
	 * @param userId identifiant de l’utilisateur
	 * @return {@code true} si le document existe et que son ownerId correspond à {@code userId}
	 */
	public boolean isDocumentOwner(Integer docId, Integer userId) {
		return documentRepository.existsByIdAndUserId(docId, userId);
	}

	/**
	 * Vérifie qu’une enchère appartient à un trader donné.
	 *
	 * @param traderId  identifiant du trader
	 * @param auctionId identifiant de l’enchère
	 * @return {@code true} si l’enchère existe et que traderId est bien l’auteur
	 */
	public boolean isAuctionOwner(Integer traderId, Integer auctionId) {
		if (traderId == null || auctionId == null) return false;
		return auctionRepository.existsByIdAndTraderId(auctionId, traderId);
	}

	/**
	 * Vérifie qu’une offre (bid) appartient à un trader donné.
	 *
	 * @param traderId identifiant du trader
	 * @param bidId    identifiant de l’offre
	 * @return {@code true} si l’offre existe et que traderId est bien celui qui l’a placée
	 */
	public boolean isBidOwner(Integer traderId, Integer bidId) {
		if (traderId == null || bidId == null) return false;
		return bidRepository.existsByIdAndTraderId(bidId, traderId);
	}

	/**
	 * Vérifie qu’une offre appartient à un trader et que le trader possède
	 * l’enchère associée à cette offre.
	 *
	 * @param traderId identifiant du trader
	 * @param bidId    identifiant de l’offre
	 * @return {@code true} si l’utilisateur est propriétaire de l’enchère liée à l’offre
	 */
	public boolean isBidAuctionOwner(Integer traderId, Integer bidId) {
		if (traderId == null || bidId == null) return false;

		Optional<Bid> bid = bidRepository.findById(bidId);
		if (!bid.isPresent()) return false;
		return auctionRepository.existsByIdAndTraderId(bid.get().getAuctionId(), traderId);
	}

	/**
	 * Vérifie si un bidDto est autorisé pour un trader.
	 * <ul>
	 *   <li>Le trader ne peut pas enchérir pour quelqu’un d’autre.</li>
	 *   <li>Le trader ne peut pas enchérir sur sa propre enchère.</li>
	 *   <li>Le trader ne peut pas placer deux offres consécutives.</li>
	 * </ul>
	 *
	 * @param traderId identifiant du trader
	 * @param bidDto   DTO contenant les informations de mise
	 * @return {@code true} si toutes les règles d’autorisation sont respectées
	 */
	public boolean isBidAuthorized(Integer traderId, BidUpdateDto bidDto) {
		if (traderId == null || bidDto == null) return false;
		if (!traderId.equals(bidDto.getTraderId())) return false;
		if (isAuctionOwner(traderId, bidDto.getTraderId())) return false;
		List<Bid> bids = bidRepository.findByAuctionIdOrderByIdAsc(bidDto.getAuctionId());
		if (bids.size() > 0 && bids.getLast().getTrader().getId().equals(traderId)) return false;

		return true;
	}

	/**
	 * Vérifie qu’un produit (brut ou transformé) appartient à l’utilisateur.
	 *
	 * @param userId    identifiant de l’utilisateur
	 * @param productId identifiant du produit
	 * @return {@code true} si l’utilisateur est producteur ou transformateur du produit
	 */
	public boolean isProductOwner(Integer userId, Integer productId) {
		if (userId == null || productId == null) return false;
		return harvestProductRepository.existsByIdAndProducerId(productId, userId)
				|| transformedProductRepository.existsByIdAndTransformerId(productId, userId);
	}

	/**
	 * Vérifie qu’un DTO de mise à jour de produit appartient à l’utilisateur.
	 *
	 * @param userId     identifiant de l’utilisateur
	 * @param productDto DTO de mise à jour du produit
	 * @return {@code true} si l’utilisateur correspond au producteur ou transformateur du DTO
	 */
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

	/**
	 * Vérifie qu’un magasin appartient à un utilisateur.
	 *
	 * @param userId  identifiant de l’utilisateur
	 * @param storeId identifiant du magasin
	 * @return {@code true} si le magasin existe et que userId en est le propriétaire
	 */
	public boolean isStoreOwner(Integer userId, Integer storeId) {
		if (userId == null || storeId == null) return false;
		return storeRepository.existsByIdAndUserId(storeId, userId);
	}
}
