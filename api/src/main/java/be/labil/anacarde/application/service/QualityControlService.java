package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.db.QualityControlDto;
import be.labil.anacarde.domain.dto.write.QualityControlUpdateDto;
import java.util.List;

/**
 * Ce service offre des méthodes permettant de créer, récupérer, mettre à jour et supprimer des
 * contrôles qualité.
 */
public interface QualityControlService {

	/**
	 * Crée un nouveau contrôle qualité dans le système.
	 *
	 * @param qualityControlDto
	 *            Le DTO contenant les informations du contrôle qualité à créer.
	 * @return Un QualityControlDto représentant le contrôle créé.
	 */
	QualityControlDto createQualityControl(QualityControlUpdateDto qualityControlDto);

	/**
	 * Récupère un contrôle qualité par son identifiant.
	 *
	 * @param id
	 *            L'identifiant unique du contrôle qualité.
	 * @return Un QualityControlDto correspondant à l'identifiant fourni.
	 */
	QualityControlDto getQualityControlById(Integer id);

	/**
	 * Récupère tous les contrôles qualité associés à un produit.
	 *
	 * @param productId
	 *            L'identifiant du produit.
	 * @return Une liste de QualityControlDto associés à cet utilisateur.
	 */
	List<QualityControlDto> listQualityControls(Integer productId);

	/**
	 * Met à jour un contrôle qualité existant.
	 *
	 * @param id
	 *            L'identifiant du contrôle qualité à mettre à jour.
	 * @param qualityControlDto
	 *            Le DTO contenant les nouvelles données.
	 * @return Le QualityControlDto mis à jour.
	 */
	QualityControlDto updateQualityControl(Integer id, QualityControlUpdateDto qualityControlDto);

	/**
	 * Supprime un contrôle qualité existant.
	 *
	 * @param id
	 *            L'identifiant du contrôle qualité à supprimer.
	 */
	void deleteQualityControl(Integer id);
}
