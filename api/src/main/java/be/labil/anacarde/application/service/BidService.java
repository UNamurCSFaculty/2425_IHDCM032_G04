
package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.db.BidDto;
import be.labil.anacarde.domain.dto.write.BidUpdateDto;
import java.util.List;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Ce service offre des méthodes permettant de créer, récupérer, mettre à jour et supprimer des
 * informations d'offres.
 */
public interface BidService {

	/**
	 * Crée une nouvelle offre dans le système en utilisant le BidDto fourni.
	 *
	 * @param bidDto
	 *            Le BidDto contenant les informations de la nouvelle offre.
	 * @return Un BidDto représentant l'offre créée.
	 */
	@PreAuthorize("@authz.isAdmin(principal) or (#bidDto.traderId.equals(principal.id))")
	BidDto createBid(@Param("bidDto") BidUpdateDto bidDto);

	/**
	 * Retourne l'offre correspondant à l'ID fourni.
	 *
	 * @param bidId
	 *            L'identifiant unique de l'offre.
	 * @return Un BidDto représentant l'offre avec l'ID spécifié.
	 */
	BidDto getBidById(Integer bidId);

	/**
	 * Récupère toutes les offres du système.
	 *
	 * @return Une List de BidDto représentant toutes les offres.
	 */
	List<BidDto> listBids(Integer auctionId);

	/**
	 * Mise à jour de l'offre identifiée par l'ID donné avec les informations fournies dans le
	 * BidDto.
	 *
	 * @param bidId
	 *            L'identifiant unique de l'offre à mettre à jour.
	 * @param bidDto
	 *            Le BidDto contenant les informations mises à jour.
	 * @return Un BidDto représentant l'offre mis à jour.
	 */
	@PreAuthorize("@authz.isAdmin(principal) or (#bidDto.traderId.equals(principal.id))")
	BidDto updateBid(Integer bidId, @Param("bidDto") BidUpdateDto bidDto);

	/**
	 * Accepter l'offre identifiée par l'ID donné. Une offre ne peut être acceptée que par le
	 * créateur de l'enchère correspondante.
	 *
	 * @param bidId
	 *            L'identifiant unique de l'offre à mettre à jour.
	 * @return Un BidDto représentant l'offre mis à jour.
	 */
	@PreAuthorize("@authz.isAdmin(principal) or (@ownership.isBidAuctionOwner(principal.id, #bidId))")
	BidDto acceptBid(@Param("bidId") Integer bidId);

	/**
	 * Rejeter l'offre identifiée par l'ID donné. Une offre ne peut être rejetée que par le créateur
	 * de l'enchère correspondante.
	 *
	 * @param bidId
	 *            L'identifiant unique de l'offre à mettre à jour.
	 * @return Un BidDto représentant l'offre mis à jour.
	 */
	@PreAuthorize("@authz.isAdmin(principal) or (@ownership.isBidAuctionOwner(principal.id, #bidId))")
	BidDto rejectBid(@Param("bidId") Integer bidId);

	/**
	 * Supprime l'offre identifiée par l'ID donné du système.
	 *
	 * @param bidId
	 *            L'identifiant unique de l'offre à supprimer.
	 */
	@PreAuthorize("@authz.isAdmin(principal) or (@ownership.isBidOwner(principal.id, #bidId))")
	void deleteBid(@Param("bidId") Integer bidId);
}
