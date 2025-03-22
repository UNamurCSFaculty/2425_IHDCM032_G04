package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.DocumentDto;
import java.util.List;

/**
 * Interface définissant les opérations de gestion des documents de qualité.
 */
public interface DocumentService {

	/**
	 * Crée un nouveau document de qualité.
	 *
	 * @param documentDto
	 *            Le DTO du document à créer.
	 * @return Le DTO du document créé.
	 */
	DocumentDto createDocument(DocumentDto documentDto);

	/**
	 * Récupère un document de qualité par son ID.
	 *
	 * @param id
	 *            L'identifiant du document.
	 * @return Le DTO du document correspondant.
	 */
	DocumentDto getDocumentById(Integer id);

	/**
	 * Récupère la liste de tous les documents de qualité.
	 *
	 * @return Liste des DTO des documents.
	 */
	List<DocumentDto> listDocuments();

	/**
	 * Met à jour un document de qualité existant.
	 *
	 * @param id
	 *            L'identifiant du document à mettre à jour.
	 * @param documentDto
	 *            Le DTO contenant les nouvelles informations.
	 * @return Le DTO du document mis à jour.
	 */
	DocumentDto updateDocument(Integer id, DocumentDto documentDto);

	/**
	 * Supprime un document de qualité par son ID.
	 *
	 * @param id
	 *            L'identifiant du document à supprimer.
	 */
	void deleteDocument(Integer id);
}
