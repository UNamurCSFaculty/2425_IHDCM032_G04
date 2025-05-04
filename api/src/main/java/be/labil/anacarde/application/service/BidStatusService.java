
package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.BidStatusDto;
import java.util.List;

/**
 * Ce service offre des méthodes permettant de créer, récupérer, mettre à jour et supprimer des informations de status
 * d'offres.
 */
public interface BidStatusService {

	/**
	 * Crée un nouveau status dans le système en utilisant le BidStatusDto fourni.
	 *
	 * @param BidStatusDto
	 *            Le BidStatusDto contenant les informations du nouveau status.
	 * @return Un BidStatusDto représentant le status créé.
	 */
	BidStatusDto createBidStatus(BidStatusDto BidStatusDto);

	/**
	 * Retourne le status correspondant à l'ID fourni.
	 *
	 * @param id
	 *            L'identifiant unique du status.
	 * @return Un BidStatusDto représentant le status avec l'ID spécifié.
	 */
	BidStatusDto getBidStatusById(Integer id);

	/**
	 * Récupère tous les status du système.
	 *
	 * @return Une List de BidStatusDto représentant tous les status.
	 */
	List<BidStatusDto> listBidStatus();

	/**
	 * Mise à jour du status identifié par l'ID donné avec les informations fournies dans le BidStatusDto.
	 *
	 * @param id
	 *            L'identifiant unique du status à mettre à jour.
	 * @param bidDto
	 *            Le BidStatusDto contenant les informations mises à jour.
	 * @return Un BidStatusDto représentant le status mis à jour.
	 */
	BidStatusDto updateBidStatus(Integer id, BidStatusDto bidDto);

	/**
	 * Supprime le status identifié par l'ID donné du système.
	 *
	 * @param id
	 *            L'identifiant unique dus tatus à supprimer.
	 */
	void deleteBidStatus(Integer id);
}
