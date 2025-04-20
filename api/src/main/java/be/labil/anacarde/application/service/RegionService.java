package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.RegionDto;
import java.util.List;

/**
 * Ce service offre des méthodes pour créer, récupérer, mettre à jour et supprimer des régions.
 */
public interface RegionService {

	/**
	 * Crée une nouvelle région dans le système à partir des données fournies.
	 *
	 * @param regionDto
	 *            Le DTO contenant les informations de la région à créer.
	 * @return Un RegionDto représentant la région créée.
	 */
	RegionDto createRegion(RegionDto regionDto);

	/**
	 * Récupère les détails d'une région à partir de son identifiant.
	 *
	 * @param id
	 *            L'identifiant unique de la région.
	 * @return Un RegionDto représentant la région avec l'ID spécifié.
	 */
	RegionDto getRegion(Integer id);

	/**
	 * Récupère la liste de toutes les régions du système.
	 *
	 * @param carrierId
	 *            Si mentionné, filtre les régions par transporteur.
	 * @return Une liste de RegionDto représentant toutes les régions.
	 */
	List<RegionDto> listRegions(Integer carrierId);

	/**
	 * Met à jour une région existante avec les nouvelles informations fournies.
	 *
	 * @param id
	 *            L'identifiant de la région à mettre à jour.
	 * @param regionDto
	 *            Le DTO contenant les nouvelles données.
	 * @return Un RegionDto représentant la région mise à jour.
	 */
	RegionDto updateRegion(Integer id, RegionDto regionDto);

	/**
	 * Supprime une région du système à partir de son identifiant.
	 *
	 * @param id
	 *            L'identifiant de la région à supprimer.
	 */
	void deleteRegion(Integer id);

	/**
	 * Ajoute une région existante à un transporteur donné.
	 *
	 * @param carrierId
	 *            L'identifiant du transporteur.
	 * @param regionId
	 *            L'identifiant de la région à associer au transporteur.
	 */
	void addCarrier(Integer carrierId, Integer regionId);
}
