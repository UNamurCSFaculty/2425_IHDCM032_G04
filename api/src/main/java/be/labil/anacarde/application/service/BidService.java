
package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.db.BidDto;
import be.labil.anacarde.domain.dto.write.BidUpdateDto;
import java.util.List;

/**
 * Ce service offre des méthodes permettant de créer, récupérer, mettre à jour et supprimer des
 * informations d'offres.
 */
public interface BidService {

	/**
	 * Crée une nouvelle offre dans le système en utilisant le BidDto fourni.
	 *
	 * @param BidDto
	 *            Le BidDto contenant les informations de la nouvelle offre.
	 * @return Un BidDto représentant l'offre créée.
	 */
	BidDto createBid(BidUpdateDto BidDto);

	/**
	 * Retourne l'offre correspondant à l'ID fourni.
	 *
	 * @param id
	 *            L'identifiant unique de l'offre.
	 * @return Un BidDto représentant l'offre avec l'ID spécifié.
	 */
	BidDto getBidById(Integer id);

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
	 * @param id
	 *            L'identifiant unique de l'offre à mettre à jour.
	 * @param bidDto
	 *            Le BidDto contenant les informations mises à jour.
	 * @return Un BidDto représentant l'offre mis à jour.
	 */
	BidDto updateBid(Integer id, BidUpdateDto bidDto);

	/**
	 * Accepter l'offre identifiée par l'ID donné.
	 *
	 * @param id
	 *            L'identifiant unique de l'offre à mettre à jour.
	 * @return Un BidDto représentant l'offre mis à jour.
	 */
	BidDto acceptBid(Integer id);

	/**
	 * Rejeter l'offre identifiée par l'ID donné.
	 *
	 * @param id
	 *            L'identifiant unique de l'offre à mettre à jour.
	 * @return Un BidDto représentant l'offre mis à jour.
	 */
	BidDto rejectBid(Integer id);

	/**
	 * Supprime l'offre identifiée par l'ID donné du système.
	 *
	 * @param id
	 *            L'identifiant unique de l'offre à supprimer.
	 */
	void deleteBid(Integer id);
}
