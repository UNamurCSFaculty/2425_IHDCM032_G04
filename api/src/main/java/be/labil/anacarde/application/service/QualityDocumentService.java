package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.DocumentDto;
import be.labil.anacarde.domain.dto.QualityDocumentDto;
import java.util.List;

/**
 * Interface définissant les opérations de gestion des documents de qualité.
 */
public interface QualityDocumentService {

	/**
	 * Crée un nouveau document de qualité.
	 *
	 * @param qualityDocumentDto
	 *            Le DTO du document à créer.
	 * @return Le DTO du document créé.
	 */
	DocumentDto createQualityDocument(QualityDocumentDto qualityDocumentDto);

	/**
	 * Récupère un document de qualité par son ID.
	 *
	 * @param id
	 *            L'identifiant du document.
	 * @return Le DTO du document correspondant.
	 */
	QualityDocumentDto getQualityDocumentById(Integer id);

	/**
	 * Récupère la liste de tous les documents de qualité.
	 *
	 * @return Liste des DTO des documents.
	 */
	List<QualityDocumentDto> listQualityDocuments();

	/**
	 * Met à jour un document de qualité existant.
	 *
	 * @param id
	 *            L'identifiant du document à mettre à jour.
	 * @param qualityDocumentDto
	 *            Le DTO contenant les nouvelles informations.
	 * @return Le DTO du document mis à jour.
	 */
	QualityDocumentDto updateQualityDocument(Integer id, QualityDocumentDto qualityDocumentDto);

	/**
	 * Supprime un document de qualité par son ID.
	 *
	 * @param id
	 *            L'identifiant du document à supprimer.
	 */
	void deleteQualityDocument(Integer id);
}
