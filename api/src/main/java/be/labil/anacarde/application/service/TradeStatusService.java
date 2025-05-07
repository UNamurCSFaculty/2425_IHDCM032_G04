
package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.TradeStatusDto;
import java.util.List;

/**
 * Ce service offre des méthodes permettant de créer, récupérer, mettre à jour et supprimer des informations de status
 * d'offres.
 */
public interface TradeStatusService {

	/**
	 * Crée un nouveau status dans le système en utilisant le TradeStatusDto fourni.
	 *
	 * @param TradeStatusDto
	 *            Le TradeStatusDto contenant les informations du nouveau status.
	 * @return Un TradeStatusDto représentant le status créé.
	 */
	TradeStatusDto createTradeStatus(TradeStatusDto TradeStatusDto);

	/**
	 * Retourne le status correspondant à l'ID fourni.
	 *
	 * @param id
	 *            L'identifiant unique du status.
	 * @return Un TradeStatusDto représentant le status avec l'ID spécifié.
	 */
	TradeStatusDto getTradeStatusById(Integer id);

	/**
	 * Récupère tous les status du système.
	 *
	 * @return Une List de TradeStatusDto représentant tous les status.
	 */
	List<TradeStatusDto> listTradeStatus();

	/**
	 * Mise à jour du status identifié par l'ID donné avec les informations fournies dans le TradeStatusDto.
	 *
	 * @param id
	 *            L'identifiant unique du status à mettre à jour.
	 * @param bidDto
	 *            Le TradeStatusDto contenant les informations mises à jour.
	 * @return Un TradeStatusDto représentant le status mis à jour.
	 */
	TradeStatusDto updateTradeStatus(Integer id, TradeStatusDto bidDto);

	/**
	 * Supprime le status identifié par l'ID donné du système.
	 *
	 * @param id
	 *            L'identifiant unique dus tatus à supprimer.
	 */
	void deleteTradeStatus(Integer id);
}
