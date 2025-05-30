
package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.db.ContractOfferDto;
import be.labil.anacarde.domain.dto.write.ContractOfferUpdateDto;
import java.util.List;

/**
 * Ce service offre des méthodes permettant de créer, récupérer, mettre à jour et supprimer des
 * informations de contrat.
 */
public interface ContractOfferService {

	/**
	 * Crée un nouveau contrat dans le système en utilisant le ContractOfferDto fourni.
	 *
	 * @param dto
	 *            Le ContractOfferDto contenant les informations du nouveau contrat.
	 * @return Un ContractOfferDto représentant le contrat créé.
	 */
	ContractOfferDto createContractOffer(ContractOfferUpdateDto dto);

	/**
	 * Retourne le contrat correspondant à l'ID fourni.
	 *
	 * @param id
	 *            L'identifiant unique du contrat.
	 * @return Un ContractOfferDto représentant le contrat avec l'ID spécifié.
	 */
	ContractOfferDto getContractOfferById(Integer id);

	/**
	 * Récupère tous les contrats du système.
	 *
	 * @return Une List de ContractOfferDto représentant tous les contrats.
	 */
	List<ContractOfferDto> listContractOffers(Integer traderId);

	/**
	 * Met à jour du contrat identifié par l'ID donné avec les informations fournies dans le
	 * ContractOfferDto.
	 *
	 * @param id
	 *            L'identifiant unique du contrat à mettre à jour.
	 * @param dto
	 *            Le ContractOfferDto contenant les informations mises à jour.
	 * @return Un ContractOfferDto représentant le contrat mis à jour.
	 */
	ContractOfferDto updateContractOffer(Integer id, ContractOfferUpdateDto dto);

	/**
	 * Supprime le contrat identifié par l'ID donné du système.
	 *
	 * @param id
	 *            L'identifiant unique du contrat à supprimer.
	 */
	void deleteContractOffer(Integer id);
}
