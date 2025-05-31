package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.NewsCategory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link NewsCategory} entity.
 */
@Repository
public interface NewsCategoryRepository extends JpaRepository<NewsCategory, Integer> {
	Optional<NewsCategory> findByName(String name);
}
