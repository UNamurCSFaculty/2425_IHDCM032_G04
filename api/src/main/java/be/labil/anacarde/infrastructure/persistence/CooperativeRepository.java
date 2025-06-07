package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Cooperative;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository Spring Data pour l’entité {@link Cooperative}.
 * <p>
 * Étend {@link JpaRepository} pour fournir les opérations CRUD de base
 * sur les coopératives et définit une méthode native pour mettre à jour
 * la date de création directement en base.
 */
public interface CooperativeRepository extends JpaRepository<Cooperative, Integer> {
	/**
	 * Met à jour nativement la date de création ({@code creation_date})
	 * d’une coopérative identifiée par son ID.
	 * <p>
	 * Utilise une requête SQL native pour modifier directement la colonne.
	 *
	 * @param id      identifiant de la coopérative à mettre à jour
	 * @param newDate nouvelle date de création à appliquer
	 */
	@Modifying
	@Transactional
	@Query(value = """
			  	UPDATE cooperative
			    SET creation_date = :newDate
			   	WHERE id = :id
			""", nativeQuery = true)
	void overrideCreationDateNative(@Param("id") Integer id,
			@Param("newDate") LocalDateTime newDate);
}
