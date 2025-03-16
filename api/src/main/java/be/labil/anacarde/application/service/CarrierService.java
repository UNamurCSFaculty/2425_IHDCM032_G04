package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.CarrierDto;
import java.util.List;

/**
 * Cette interface définit des méthodes pour créer, récupérer, mettre à jour et supprimer les informations d'un
 * transporteur (Carrier). Les méthodes opèrent sur des objets CarrierDto.
 */
public interface CarrierService {

	/**
	 * Crée un nouveau transporteur dans le système en utilisant le CarrierDto fourni.
	 *
	 * @param carrierDto
	 *            Le CarrierDto contenant les informations du nouveau transporteur.
	 * @return Un CarrierDto représentant le transporteur créé.
	 */
	CarrierDto createCarrier(CarrierDto carrierDto);

	/**
	 * Retourne le transporteur correspondant à l'ID fourni.
	 *
	 * @param id
	 *            L'identifiant unique du transporteur.
	 * @return Un CarrierDto représentant le transporteur avec l'ID spécifié.
	 */
	CarrierDto getCarrierById(Integer id);

	/**
	 * Récupère tous les transporteurs du système.
	 *
	 * @return Une List de CarrierDto représentant tous les transporteurs.
	 */
	List<CarrierDto> listCarriers();

	/**
	 * Met à jour le transporteur identifié par l'ID donné avec les informations fournies dans le CarrierDto.
	 *
	 * @param id
	 *            L'identifiant unique du transporteur à mettre à jour.
	 * @param carrierDto
	 *            Le CarrierDto contenant les informations mises à jour.
	 * @return Un CarrierDto représentant le transporteur mis à jour.
	 */
	CarrierDto updateCarrier(Integer id, CarrierDto carrierDto);

	/**
	 * Supprime le transporteur identifié par l'ID donné du système.
	 *
	 * @param id
	 *            L'identifiant unique du transporteur à supprimer.
	 */
	void deleteCarrier(Integer id);
}
