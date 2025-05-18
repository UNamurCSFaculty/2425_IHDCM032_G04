package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.City;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Integer> {
	Optional<City> findByName(String name);
}
