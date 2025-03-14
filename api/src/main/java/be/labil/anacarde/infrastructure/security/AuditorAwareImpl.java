package be.labil.anacarde.infrastructure.security;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
/**
 * Cette classe fournit le nom de l'utilisateur authentifié actuel, à utiliser comme auditeur pour
 * l'audit des données. Si aucun utilisateur authentifié n'est disponible ou si l'utilisateur est
 * anonyme, une valeur par défaut est renvoyée.
 */
public class AuditorAwareImpl implements AuditorAware<String> {

    /**
     * Obtient l'authentification courante depuis le SecurityContextHolder et vérifie si
     * l'utilisateur est authentifié et non anonyme. Si l'authentification est nulle, non
     * authentifiée ou représente un utilisateur anonyme, une valeur d'auditeur par défaut est
     * renvoyée. Sinon, le nom de l'utilisateur authentifié est retourné.
     *
     * @return Un Optional contenant le nom de l'auditeur si disponible, sinon un Optional contenant
     *     une valeur par défaut.
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Vérifier si l'utilisateur est authentifié et non anonyme
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.of("anonyme");
        }

        return Optional.ofNullable(authentication.getName());
    }
}
