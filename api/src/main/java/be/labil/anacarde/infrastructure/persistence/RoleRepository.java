package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @brief Repository interface for Role entities.
 *
 */
public interface RoleRepository extends JpaRepository<Role, Integer> {

    /**
     * @brief Finds a role by its name.
     *
     * This method searches for a Role entity with the specified name.
     *
     * @param name The name of the role to search for.
     * @return The Role if found; otherwise, null.
     */
    Optional<Role> findByName(String name);
}
