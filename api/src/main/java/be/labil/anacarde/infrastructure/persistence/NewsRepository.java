package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link News} entity.
 */
@Repository
public interface NewsRepository
		extends
			JpaRepository<News, Integer>,
			JpaSpecificationExecutor<News> {

	/**
	 * Finds all news articles, optionally filtered by category ID, and paginated.
	 *
	 * @param categoryId
	 *            The ID of the category to filter by (optional).
	 * @param pageable
	 *            The pagination information.
	 * @return A page of news articles.
	 */
	@Query("SELECT n FROM News n WHERE (:categoryId IS NULL OR n.category.id = :categoryId)")
	Page<News> findByCategoryId(@Param("categoryId") Integer categoryId, Pageable pageable);
}
