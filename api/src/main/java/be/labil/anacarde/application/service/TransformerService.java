package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.TransformerDto;
import java.util.List;

/**
 * Interface définissant les opérations de gestion des transformateurs.
 */
public interface TransformerService {

	/**
	 * Crée un nouveau transformateur dans le système.
	 *
	 * @param transformerDto
	 *            Le TransformerDto contenant les informations du nouveau transformateur.
	 * @return Un TransformerDto représentant le transformateur créé.
	 */
	TransformerDto createTransformer(TransformerDto transformerDto);

	/**
	 * Retourne le transformateur correspondant à l'ID fourni.
	 *
	 * @param id
	 *            L'identifiant unique du transformateur.
	 * @return Un TransformerDto représentant le transformateur avec l'ID spécifié.
	 */
	TransformerDto getTransformerById(Integer id);

	/**
	 * Récupère tous les transformateurs du système.
	 *
	 * @return Une liste de TransformerDto représentant tous les transformateurs.
	 */
	List<TransformerDto> listTransformers();

	/**
	 * Met à jour un transformateur identifié par l'ID donné.
	 *
	 * @param id
	 *            L'identifiant unique du transformateur à mettre à jour.
	 * @param transformerDto
	 *            Le TransformerDto contenant les nouvelles informations.
	 * @return Un TransformerDto représentant le transformateur mis à jour.
	 */
	TransformerDto updateTransformer(Integer id, TransformerDto transformerDto);

	/**
	 * Supprime un transformateur du système.
	 *
	 * @param id
	 *            L'identifiant unique du transformateur à supprimer.
	 */
	void deleteTransformer(Integer id);
}