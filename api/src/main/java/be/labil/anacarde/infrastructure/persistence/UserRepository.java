package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository interface for User entities. */
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * This method searches for a User entity with the specified email address.
     *
     * @param email The email address of the user to search for.
     * @return An Optional containing the User if found; otherwise, an empty Optional.
     */
    Optional<User> findByEmail(String email);
}
