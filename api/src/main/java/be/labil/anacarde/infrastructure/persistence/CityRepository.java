package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.City;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository Spring Data pour l’entité {@link City}.
 * <p>
 * Étend {@link JpaRepository} pour fournir les opérations CRUD de base
 * et ajoute une méthode de recherche par nom de ville.
 */
public interface CityRepository extends JpaRepository<City, Integer> {
	Optional<City> findByName(String name);
}
