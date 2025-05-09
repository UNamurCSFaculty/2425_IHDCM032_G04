package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.db.DocumentDto;
import java.util.List;

/**
 * Ce service offre des méthodes permettant de créer, récupérer, mettre à jour, supprimer et lister des documents selon
 * différents critères.
 */
public interface DocumentService {

	/**
	 * Crée un nouveau document dans le système en utilisant le DocumentDto fourni.
	 *
	 * @param documentDto
	 *            Le DocumentDto contenant les informations du nouveau document.
	 * @return Un DocumentDto représentant le document créé.
	 */
	DocumentDto createDocument(DocumentDto documentDto);

	/**
	 * Retourne le document correspondant à l'ID fourni.
	 *
	 * @param id
	 *            L'identifiant unique du document.
	 * @return Un DocumentDto représentant le document avec l'ID spécifié.
	 */
	DocumentDto getDocumentById(Integer id);

	/**
	 * Met à jour le document identifié par l'ID donné avec les informations fournies dans le DocumentDto.
	 *
	 * @param id
	 *            L'identifiant unique du document à mettre à jour.
	 * @param documentDto
	 *            Le DocumentDto contenant les informations mises à jour.
	 * @return Un DocumentDto représentant le document mis à jour.
	 */
	DocumentDto updateDocument(Integer id, DocumentDto documentDto);

	/**
	 * Supprime le document identifié par l'ID donné du système.
	 *
	 * @param id
	 *            L'identifiant unique du document à supprimer.
	 */
	void deleteDocument(Integer id);

	// /**
	// * Récupère tous les documents associés à un contrôle qualité donné.
	// *
	// * @param qualityControlId
	// * L'identifiant du contrôle qualité.
	// * @return Une liste de DocumentDto correspondant aux documents liés à ce contrôle qualité.
	// */
	// List<DocumentDto> listDocumentsByQualityControl(Integer qualityControlId);

	/**
	 * Récupère tous les documents associés à un utilisateur donné.
	 *
	 * @param userId
	 *            L'identifiant de l'utilisateur.
	 * @return Une liste de DocumentDto correspondant aux documents liés à cet utilisateur.
	 */
	List<DocumentDto> listDocumentsByUser(Integer userId);
}
