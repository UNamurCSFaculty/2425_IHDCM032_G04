package be.labil.anacarde.infrastructure.util;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

/**
 * Helper utilitaire pour la persistance JPA.
 * <p>
 * Fournit une méthode générique pour sauvegarder une entité, vider le contexte de persistance
 * (flush + clear) puis la recharger depuis le repository afin de récupérer son état réellement
 * stocké en base.
 */
@Component
@RequiredArgsConstructor
public class PersistenceHelper {

	@Autowired
	private EntityManager em;

	/**
	 * Sauve l’entité, flush/clear le contexte, puis recharge l’entité depuis le repo.
	 *
	 * @param <T>
	 *            type d’entité
	 * @param <ID>
	 *            type de la clé
	 * @param repo
	 *            JpaRepository de l’entité
	 * @param entity
	 *            instance à sauver
	 * @param idExtractor
	 *            fonction pour extraire l’ID de l’entité sauvée
	 */
	public <T, ID> T saveAndReload(JpaRepository<T, ID> repo, T entity,
			Function<T, ID> idExtractor) {

		T saved = repo.save(entity);
		em.flush();
		em.clear();
		ID id = idExtractor.apply(saved);
		return repo.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("Impossible de recharger l’entité id=" + id));
	}
}
