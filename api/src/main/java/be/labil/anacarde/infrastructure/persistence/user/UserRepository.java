package be.labil.anacarde.infrastructure.persistence.user;

import be.labil.anacarde.domain.model.AuthProvider;
import be.labil.anacarde.domain.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

/** Interface Repository pour les entités User. */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	/** Recherche un User (tous providers confondus) par e-mail, fetch roles. */
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
	Optional<User> findByEmail(String email);

	/** Recherche un User “local” par e-mail. */
	default Optional<User> findLocalByEmail(String email) {
		return findByEmailAndProvider(email, AuthProvider.LOCAL);
	}

	/** Recherche un User Google par son providerId (le “sub”). */
	default Optional<User> findGoogleByProviderId(String providerId) {
		return findByProviderAndProviderId(AuthProvider.GOOGLE, providerId);
	}

	/**
	 * Recherche un User par e-mail **et** provider. Utile pour distinguer local vs. social.
	 */
	Optional<User> findByEmailAndProvider(String email, AuthProvider provider);

	/**
	 * Recherche un User par provider **et** providerId (ex. GOOGLE + “1234567890”).
	 */
	@EntityGraph(attributePaths = {"roles", "documents", "address", "address.city",
			"address.region", "language"})
	Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);

	/**
	 * Recherche un User dont le numéro de téléphone correspond à celui fourni. (inchangé)
	 */
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.phone = :phone")
	Optional<User> findByPhone(String phone);

	@Override
	@NonNull
	@EntityGraph(attributePaths = {"roles", "documents", "address", "address.city",
			"address.region", "language"})
	Optional<User> findById(@NonNull Integer id);

	/**
	 * Vérifie si un utilisateur existe avec l'adresse e-mail fournie.
	 *
	 * @param email
	 *            L'adresse e-mail à vérifier.
	 * @return true si un utilisateur existe avec cette adresse e-mail, false sinon.
	 */
	boolean existsByEmail(String email);

	/**
	 * Vérifie si un utilisateur existe avec le numéro de téléphone fourni.
	 * 
	 * @param phone
	 * @return true si un utilisateur existe avec ce numéro de téléphone, false sinon.
	 */
	boolean existsByPhone(String phone);
}
