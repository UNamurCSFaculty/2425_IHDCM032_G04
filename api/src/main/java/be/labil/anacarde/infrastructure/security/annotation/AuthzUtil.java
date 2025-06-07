package be.labil.anacarde.infrastructure.security.annotation;

import be.labil.anacarde.domain.model.Admin;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Utilitaire pour les expressions de contrôle d’accès (SpEL) basées sur le rôle d’utilisateur.
 * <p>
 * Exposé sous le nom "authz" pour être utilisé dans les annotations Spring Security
 * (par exemple `@PreAuthorize("@authz.isAdmin(principal)")`).
 */
@Component("authz") // nom d’accès depuis SpEL
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthzUtil {

	/**
	 * Vérifie si l’utilisateur courant (principal) est un administrateur.
	 *
	 * @param principal les informations de l’utilisateur authentifié
	 * @return {@code true} si le principal est une instance de {@link Admin}, {@code false} sinon
	 */
	public boolean isAdmin(UserDetails principal) {
		return principal instanceof Admin;
	}
}