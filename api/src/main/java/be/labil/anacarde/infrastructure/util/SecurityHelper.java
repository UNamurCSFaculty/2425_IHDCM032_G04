package be.labil.anacarde.infrastructure.util;

import be.labil.anacarde.application.exception.ApiErrorCode;
import be.labil.anacarde.application.exception.ApiErrorException;
import be.labil.anacarde.application.exception.ErrorDetail;
import be.labil.anacarde.domain.model.Admin;
import be.labil.anacarde.domain.model.User;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Helper pour extraire et vérifier l’utilisateur authentifié depuis le contexte de sécurité.
 * <p>
 * Fournit des méthodes statiques pour :
 * <ul>
 * <li>Récupérer l’utilisateur courant ou lancer une erreur API.</li>
 * <li>Vérifier si l’utilisateur est un administrateur ou le compte système d’initialisation.</li>
 * </ul>
 */
@Slf4j
public class SecurityHelper {

	/**
	 * Récupère l’utilisateur authentifié depuis Spring Security.
	 * <p>
	 * Si aucun utilisateur n’est authentifié ou si le principal est invalide, lève une
	 * {@link ApiErrorException} avec le code approprié.
	 *
	 * @return l’instance {@link User} du principal authentifié
	 * @throws ApiErrorException
	 *             si la requête n’est pas authentifiée ou si le principal est invalide
	 */
	public static User getAuthenticatedUserOrFail() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			log.warn("Tentative d'accès sans authentification valide.");
			throw new ApiErrorException(HttpStatus.UNAUTHORIZED,
					ApiErrorCode.ACCESS_UNAUTHORIZED.code(),
					List.of(new ErrorDetail("auth", "auth.required", "Authentification requise.")));
		}

		Object principal = authentication.getPrincipal();

		if (principal == null) {
			log.warn(
					"Le principal d'authentification est null alors que l'authentification est marquée comme valide.");
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
					ApiErrorCode.INTERNAL_ERROR.code(), List.of(new ErrorDetail("auth",
							"auth.principal.null", "Principal d'authentification non trouvé.")));
		}

		if (principal instanceof User authenticatedUser) {
			return authenticatedUser;
		} else if (principal instanceof String && "anonymousUser".equals(principal)) {
			log.warn(
					"Tentative d'accès par un utilisateur anonyme où un utilisateur authentifié est requis.");
			throw new ApiErrorException(HttpStatus.UNAUTHORIZED,
					ApiErrorCode.ACCESS_UNAUTHORIZED.code(),
					List.of(new ErrorDetail("auth", "auth.anonymous.forbidden",
							"Accès anonyme non autorisé pour cette opération.")));
		} else {
			log.warn(
					"Le principal d'authentification n'est pas une instance de 'User'. Type actuel: {}",
					principal.getClass().getName());
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
					ApiErrorCode.INTERNAL_ERROR.code(),
					List.of(new ErrorDetail("auth", "auth.principal.invalid",
							"Type de principal d'authentification inattendu.")));
		}
	}

	/**
	 * Vérifie si l’utilisateur courant est le compte système d’initialisation de la base.
	 *
	 * @return {@code true} si l’utilisateur est une instance d’{@link Admin} avec l’email système
	 */
	public static boolean isDbInitDataUser() {
		User user = getAuthenticatedUserOrFail();
		return user instanceof Admin && user.getEmail().equals("system@anacarde.local");
	}

	/**
	 * Vérifie si l’utilisateur courant a le rôle ADMIN.
	 *
	 * @return {@code true} si l’utilisateur possède l’autorité ROLE_ADMIN
	 */
	public static boolean isAdmin() {
		User admin = getAuthenticatedUserOrFail();
		return isAdmin(admin);
	}

	/**
	 * Vérifie si un {@link User} donné a le rôle ADMIN.
	 *
	 * @param user
	 *            l’utilisateur à tester
	 * @return {@code true} si l’utilisateur est non null et a l’autorité ROLE_ADMIN
	 */
	public static boolean isAdmin(User user) {
		if (user == null || user.getAuthorities() == null) {
			return false;
		}
		return user.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
	}

	/**
	 * Vérifie si une {@link Authentication} possède le rôle ADMIN.
	 *
	 * @param authentication
	 *            le contexte d’authentification à tester
	 * @return {@code true} si l’authentication est authentifiée et contient ROLE_ADMIN
	 */
	public static boolean isAdmin(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()
				|| authentication.getAuthorities() == null) {
			return false;
		}
		return authentication.getAuthorities().stream()
				.anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
	}
}