package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Interface Repository pour les entités Role. */
public interface RoleRepository extends JpaRepository<Role, Integer> {

    /**
     * Recherche une entité Role dont le nom correspond à celui spécifié.
     *
     * @param name Le nom du rôle à rechercher.
     * @return Un Optional contenant le Role trouvé, ou Optional.empty() si aucun rôle n'est trouvé.
     */
    Optional<Role> findByName(String name);
}
