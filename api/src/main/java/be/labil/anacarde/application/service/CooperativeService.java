package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.CooperativeDto;
import java.util.List;

/**
 * Ce service offre des méthodes permettant de créer, récupérer, mettre à jour et supprimer des coopératives.
 */
public interface CooperativeService {

	/**
	 * Crée une nouvelle coopérative dans le système en utilisant le CooperativeDto fourni.
	 *
	 * @param cooperativeDto
	 *            Le CooperativeDto contenant les informations de la nouvelle coopérative.
	 * @return Un CooperativeDto représentant la coopérative créée.
	 */
	CooperativeDto createCooperative(CooperativeDto cooperativeDto);

	/**
	 * Retourne la coopérative correspondant à l'ID fourni.
	 *
	 * @param id
	 *            L'identifiant unique de la coopérative.
	 * @return Un CooperativeDto représentant la coopérative avec l'ID spécifié.
	 */
	CooperativeDto getCooperativeById(Integer id);

	/**
	 * Récupère toutes les coopératives du système.
	 *
	 * @return Une liste de CooperativeDto représentant toutes les coopératives.
	 */
	List<CooperativeDto> listCooperatives();

	/**
	 * Met à jour la coopérative identifiée par l'ID donné avec les informations fournies dans le CooperativeDto.
	 *
	 * @param id
	 *            L'identifiant unique de la coopérative à mettre à jour.
	 * @param cooperativeDto
	 *            Le CooperativeDto contenant les informations mises à jour.
	 * @return Un CooperativeDto représentant la coopérative mise à jour.
	 */
	CooperativeDto updateCooperative(Integer id, CooperativeDto cooperativeDto);

	/**
	 * Supprime la coopérative identifiée par l'ID donné du système.
	 *
	 * @param id
	 *            L'identifiant unique de la coopérative à supprimer.
	 */
	void deleteCooperative(Integer id);
}
