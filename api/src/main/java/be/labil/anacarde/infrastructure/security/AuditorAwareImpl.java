package be.labil.anacarde.infrastructure.security;


import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
/**
 * @brief Implementation of the AuditorAware interface for tracking the current auditor.
 *
 * This class provides the current authenticated user's name to be used as the auditor in data auditing.
 * If no authenticated user is available or if the user is anonymous, a default value is returned.
 */
public class AuditorAwareImpl implements AuditorAware<String> {

    /**
     * @brief Retrieves the current auditor's name.
     *
     * This method obtains the current authentication from the SecurityContextHolder and checks whether the user
     * is authenticated and not anonymous. If the authentication is null, not authenticated, or represents an anonymous user,
     * it returns a default auditor value. Otherwise, it returns the authenticated user's name.
     *
     * @return An Optional containing the auditor's name if available; otherwise, an Optional containing a default value.
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Vérifier si l'utilisateur est authentifié et non anonyme
        if (authentication == null || !authentication.isAuthenticated()
            || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.of("anonyme");
        }

        // Renvoyer le nom de l'utilisateur
        return Optional.ofNullable(authentication.getName());
    }
}
