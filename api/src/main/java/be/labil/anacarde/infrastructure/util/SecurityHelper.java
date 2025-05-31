package be.labil.anacarde.infrastructure.util;

import be.labil.anacarde.application.exception.ApiErrorCode;
import be.labil.anacarde.application.exception.ApiErrorException;
import be.labil.anacarde.application.exception.ErrorDetail;
import be.labil.anacarde.domain.model.User;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityHelper {

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

	public static boolean isAdmin(User user) {
		if (user == null || user.getAuthorities() == null) {
			return false;
		}
		return user.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
	}

	public static boolean isAdmin(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()
				|| authentication.getAuthorities() == null) {
			return false;
		}
		return authentication.getAuthorities().stream()
				.anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
	}
}