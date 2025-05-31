package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.db.NewsCategoryDto;
import java.util.List;

/**
 * Interface de service pour la gestion des catégories de nouvelles.
 */
public interface NewsCategoryService {

	/**
	 * Crée une nouvelle catégorie de nouvelles.
	 *
	 * @param newsCategoryDto
	 *            Le DTO contenant les détails de la catégorie à créer.
	 * @return Le DTO de la catégorie de nouvelles créée.
	 */
	NewsCategoryDto createNewsCategory(NewsCategoryDto newsCategoryDto);

	/**
	 * Récupère une catégorie de nouvelles par son ID.
	 *
	 * @param id
	 *            L'ID de la catégorie de nouvelles.
	 * @return Le DTO de la catégorie de nouvelles.
	 * @throws be.labil.anacarde.application.exception.ResourceNotFoundException
	 *             si la catégorie n'est pas trouvée.
	 */
	NewsCategoryDto getNewsCategoryById(Integer id);

	/**
	 * Récupère toutes les catégories de nouvelles.
	 *
	 * @return Une liste de tous les DTOs des catégories de nouvelles.
	 */
	List<NewsCategoryDto> listNewsCategories();

	/**
	 * Met à jour une catégorie de nouvelles existante.
	 *
	 * @param id
	 *            L'ID de la catégorie de nouvelles à mettre à jour.
	 * @param newsCategoryDto
	 *            Le DTO contenant les détails mis à jour.
	 * @return Le DTO de la catégorie de nouvelles mise à jour.
	 * @throws be.labil.anacarde.application.exception.ResourceNotFoundException
	 *             si la catégorie n'est pas trouvée.
	 */
	NewsCategoryDto updateNewsCategory(Integer id, NewsCategoryDto newsCategoryDto);

	/**
	 * Supprime une catégorie de nouvelles par son ID.
	 *
	 * @param id
	 *            L'ID de la catégorie de nouvelles à supprimer.
	 * @throws be.labil.anacarde.application.exception.ResourceNotFoundException
	 *             si la catégorie n'est pas trouvée.
	 * @throws be.labil.anacarde.application.exception.OperationNotAllowedException
	 *             si la catégorie est associée à des articles de nouvelles.
	 */
	void deleteNewsCategory(Integer id);
}