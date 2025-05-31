package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.db.NewsDto;
import be.labil.anacarde.domain.dto.write.NewsCreateDto;
import be.labil.anacarde.domain.dto.write.NewsFilterDto;
import be.labil.anacarde.domain.dto.write.NewsUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interface de service pour la gestion des articles de nouvelles.
 */
public interface NewsService {

	/**
	 * Crée un nouvel article de nouvelles.
	 *
	 * @param newsDto
	 *            Le DTO contenant les détails de l'article à créer.
	 * @return Le DTO de l'article de nouvelles créé.
	 */
	NewsDto createNews(NewsCreateDto newsDto);

	/**
	 * Récupère un article de nouvelles par son ID.
	 *
	 * @param id
	 *            L'ID de l'article de nouvelles.
	 * @return Le DTO de l'article de nouvelles.
	 * @throws be.labil.anacarde.application.exception.ResourceNotFoundException
	 *             si l'article n'est pas trouvé.
	 */
	NewsDto getNewsById(Integer id);

	/**
	 * Récupère une liste paginée d'articles de nouvelles, éventuellement filtrée et triée.
	 *
	 * @param requestDto
	 *            DTO contenant les paramètres de filtre (authorId, categoryId, limit).
	 * @param pageable
	 *            Informations de pagination et de tri. Le tri par défaut est publicationDate,desc.
	 * @return Une page de DTOs d'articles de nouvelles.
	 */
	Page<NewsDto> listNews(NewsFilterDto requestDto, Pageable pageable);

	/**
	 * Met à jour un article de nouvelles existant.
	 *
	 * @param id
	 *            L'ID de l'article de nouvelles à mettre à jour.
	 * @param newsUpdateDto
	 *            Le DTO contenant les détails mis à jour.
	 * @return Le DTO de l'article de nouvelles mis à jour.
	 * @throws be.labil.anacarde.application.exception.ResourceNotFoundException
	 *             si l'article n'est pas trouvé.
	 */
	NewsDto updateNews(Integer id, NewsUpdateDto newsUpdateDto);

	/**
	 * Supprime un article de nouvelles par son ID.
	 *
	 * @param id
	 *            L'ID de l'article de nouvelles à supprimer.
	 * @throws be.labil.anacarde.application.exception.ResourceNotFoundException
	 *             si l'article n'est pas trouvé.
	 */
	void deleteNews(Integer id);
}