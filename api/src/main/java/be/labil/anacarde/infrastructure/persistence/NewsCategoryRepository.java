package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.NewsCategory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository Spring Data JPA pour l’entité {@link NewsCategory}.
 * <p>
 * Fournit les opérations CRUD de base ainsi que des méthodes de recherche
 * personnalisées pour les catégories de news.
 */
@Repository
public interface NewsCategoryRepository extends JpaRepository<NewsCategory, Integer> {
	/**
	 * Recherche une catégorie de news par son nom.
	 * <p>
	 * Utile pour éviter la duplication et récupérer une catégorie existante
	 * lors de la création ou de la mise à jour des news.
	 *
	 * @param name le nom de la catégorie à rechercher
	 * @return un {@link Optional} contenant la {@link NewsCategory} si trouvée, sinon vide
	 */
	Optional<NewsCategory> findByName(String name);
}
