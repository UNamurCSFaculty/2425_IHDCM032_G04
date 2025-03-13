package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository interface for Role entities. */
public interface RoleRepository extends JpaRepository<Role, Integer> {

    /**
     * This method searches for a Role entity with the specified name.
     *
     * @param name The name of the role to search for.
     * @return The Role if found; otherwise, null.
     */
    Optional<Role> findByName(String name);
}
