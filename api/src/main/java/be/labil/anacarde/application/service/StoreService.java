
package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.db.StoreDetailDto;
import be.labil.anacarde.domain.dto.write.StoreUpdateDto;
import java.util.List;

/**
 * Ce service offre des méthodes permettant de créer, récupérer, mettre à jour et supprimer des
 * informations de magasin.
 */
public interface StoreService {

	/**
	 * Crée un nouveau magasin dans le système en utilisant le StoreDto fourni.
	 *
	 * @param storeUpdateDto
	 *            Le StoreDto contenant les informations du nouveau magasin.
	 * @return Un StoreDto représentant le magasin créé.
	 */
	StoreDetailDto createStore(StoreUpdateDto storeUpdateDto);

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
	 * @param storeUpdateDto
	 *            Le StoreDto contenant les informations mises à jour.
	 * @return Un StoreDto représentant le magasin mis à jour.
	 */
	StoreDetailDto updateStore(Integer id, StoreUpdateDto storeUpdateDto);

	/**
	 * Supprime le magasin identifié par l'ID donné du système.
	 *
	 * @param id
	 *            L'identifiant unique du magasin à supprimer.
	 */
	void deleteStore(Integer id);

	/**
	 * Vérifie si un magasin existe pour l'utilisateur spécifié.
	 *
	 * @param userId
	 *            L'identifiant de l'utilisateur.
	 * @return true si un magasin existe pour l'utilisateur, false sinon.
	 */
	boolean existsStoreByUserId(Integer userId);
}
