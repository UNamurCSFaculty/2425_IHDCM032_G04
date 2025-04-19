package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.FieldDetailDto;
import java.util.List;

/**
 * Ce service offre des méthodes permettant de créer, récupérer, mettre à jour et supprimer des informations de champ.
 */
public interface FieldService {

	/**
	 * Crée un nouveau champ dans le système en utilisant le FieldDetailDto fourni.
	 *
	 * @param fieldDetailDto
	 *            Le FieldDetailDto contenant les informations du nouveau champ.
	 * @return Un FieldDetailDto représentant le champ créé.
	 */
	FieldDetailDto createField(FieldDetailDto fieldDetailDto);

	/**
	 * Retourne le champ correspondant à l'ID fourni.
	 *
	 * @param id
	 *            L'identifiant unique du champ.
	 * @return Un FieldDetailDto représentant le champ avec l'ID spécifié.
	 */
	FieldDetailDto getFieldById(Integer id);

	/**
	 * Récupère tous les champs du système correspondant à un utilisateur.
	 *
	 * @param userId
	 *            L'identifiant de l'utilisateur.
	 * @return Une List de FieldDetailDto représentant tous les champs d'un utilisateur.
	 */
	List<FieldDetailDto> listFields(Integer userId);

	/**
	 * Met à jour le champ identifié par l'ID donné avec les informations fournies dans le FieldDetailDto.
	 *
	 * @param id
	 *            L'identifiant unique du champ à mettre à jour.
	 * @param fieldDetailDto
	 *            Le FieldDetailDto contenant les informations mises à jour.
	 * @return Un FieldDetailDto représentant le champ mis à jour.
	 */
	FieldDetailDto updateField(Integer id, FieldDetailDto fieldDetailDto);

	/**
	 * Supprime le champ identifié par l'ID donné du système.
	 *
	 * @param id
	 *            L'identifiant unique du champ à supprimer.
	 */
	void deleteField(Integer id);
}
