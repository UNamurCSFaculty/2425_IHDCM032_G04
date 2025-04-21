
package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.QualityDto;
import java.util.List;

/**
 * Ce service offre des méthodes permettant de créer, récupérer, mettre à jour et supprimer des informations de qualité.
 */
public interface QualityService {

	/**
	 * Crée une nouvelle qualité dans le système en utilisant le QualityDto fourni.
	 *
	 * @param QualityDto
	 *            Le QualityDto contenant les informations du nouveau qualité.
	 * @return Un QualityDto représentant le qualité créé.
	 */
	QualityDto createQuality(QualityDto QualityDto);

	/**
	 * Retourne la qualité correspondant à l'ID fourni.
	 *
	 * @param id
	 *            L'identifiant unique de la qualité.
	 * @return Un QualityDto représentant la qualité avec l'ID spécifié.
	 */
	QualityDto getQualityById(Integer id);

	/**
	 * Récupère toutes les qualités du système.
	 *
	 * @return Une List de QualityDto représentant toutes les qualités.
	 */
	List<QualityDto> listQualities();

	/**
	 * Met à jour de la qualité identifiée par l'ID donné avec les informations fournies dans le QualityDto.
	 *
	 * @param id
	 *            L'identifiant unique de la qualité à mettre à jour.
	 * @param QualityDto
	 *            Le QualityDto contenant les informations mises à jour.
	 * @return Un QualityDto représentant la qualité mis à jour.
	 */
	QualityDto updateQuality(Integer id, QualityDto QualityDto);

	/**
	 * Supprime la qualité identifiée par l'ID donné du système.
	 *
	 * @param id
	 *            L'identifiant unique de la qualité à supprimer.
	 */
	void deleteQuality(Integer id);
}
