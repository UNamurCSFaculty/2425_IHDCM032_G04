package be.labil.anacarde.infrastructure.persistence.user;

import be.labil.anacarde.domain.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface GenericUserRepository<T extends User> extends JpaRepository<T, Integer> {

	@EntityGraph(attributePaths = {"roles"})
	Optional<T> findByEmail(String email);

	@EntityGraph(attributePaths = {"roles", "language"})
	@Query("SELECT u FROM #{#entityName} u WHERE u.id = :id")
	Optional<T> findByIdWithAssociations(@Param("id") Integer id);
}
