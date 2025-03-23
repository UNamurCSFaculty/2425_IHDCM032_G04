package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.FieldDto;
import java.util.List;

/**
 * Interface définissant les méthodes de gestion des champs agricoles.
 */
public interface FieldService {

	/**
	 * Crée un nouveau champ.
	 *
	 * @param fieldDto
	 *            Le DTO de la champ à créer.
	 * @return Le FieldDto créé.
	 */
	FieldDto createField(FieldDto fieldDto);

	/**
	 * Récupère un champ par son identifiant.
	 *
	 * @param id
	 *            L'identifiant unique du champ.
	 * @return Le FieldDto correspondant.
	 */
	FieldDto getFieldById(Integer id);

	/**
	 * Liste toutes les champs.
	 *
	 * @return Une liste de FieldDto.
	 */
	List<FieldDto> listFields();

	/**
	 * Met à jour un champ existante.
	 *
	 * @param id
	 *            L'identifiant du champ à mettre à jour.
	 * @param fieldDto
	 *            Les nouvelles informations du champ.
	 * @return Le FieldDto mis à jour.
	 */
	FieldDto updateField(Integer id, FieldDto fieldDto);

	/**
	 * Supprime un champ.
	 *
	 * @param id
	 *            L'identifiant du champ à supprimer.
	 */
	void deleteField(Integer id);
}
