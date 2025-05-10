
package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.db.StoreDetailDto;
import java.util.List;

/**
 * Ce service offre des méthodes permettant de créer, récupérer, mettre à jour et supprimer des
 * informations de magasin.
 */
public interface StoreService {

	/**
	 * Crée un nouveau magasin dans le système en utilisant le StoreDto fourni.
	 *
	 * @param StoreDetailDto
	 *            Le StoreDto contenant les informations du nouveau magasin.
	 * @return Un StoreDto représentant le magasin créé.
	 */
	StoreDetailDto createStore(StoreDetailDto StoreDetailDto);

	/**
	 * Retourne le magasin correspondant à l'ID fourni.
	 *
	 * @param id
	 *            L'identifiant unique du magasin.
	 * @return Un StoreDto représentant le magasin avec l'ID spécifié.
	 */
	StoreDetailDto getStoreById(Integer id);

	/**
	 * Récupère tous les magasins du système.
	 *
	 * @return Une List de StoreDto représentant tous les magasins.
	 */
	List<StoreDetailDto> listStores();

	/**
	 * Met à jour du magasin identifié par l'ID donné avec les informations fournies dans le
	 * StoreDto.
	 *
	 * @param id
	 *            L'identifiant unique du magasin à mettre à jour.
	 * @param StoreDetailDto
	 *            Le StoreDto contenant les informations mises à jour.
	 * @return Un StoreDto représentant le magasin mis à jour.
	 */
	StoreDetailDto updateStore(Integer id, StoreDetailDto StoreDetailDto);

	/**
	 * Supprime le magasin identifié par l'ID donné du système.
	 *
	 * @param id
	 *            L'identifiant unique du magasin à supprimer.
	 */
	void deleteStore(Integer id);
}
