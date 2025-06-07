package be.labil.anacarde.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filtre HTTP Spring Security qui vérifie l’en-tête Origin des requêtes entrantes.
 * <p>
 * Si l’en-tête Origin est présent et ne correspond pas à l’origine de confiance configurée
 * (propriété {@code app.trusted.origin}), la requête est rejetée avec un code HTTP 403 Forbidden.
 */
@Component
@RequiredArgsConstructor
public class OriginFilter extends OncePerRequestFilter {
	@Value("${app.trusted.origin}")
	private String trustedOrigin;

	/**
	 * Intercepte chaque requête HTTP une seule fois pour valider l’en-tête Origin.
	 *
	 * @param req
	 *            la requête HTTP entrante
	 * @param res
	 *            la réponse HTTP à écrire en cas d’erreur
	 * @param chain
	 *            la chaîne de filtres à invoquer si l’origine est valide
	 * @throws ServletException
	 *             en cas d’erreur de filtrage
	 * @throws IOException
	 *             en cas d’erreur d’E/S lors de la lecture ou l’écriture
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
			FilterChain chain) throws ServletException, IOException {
		String origin = req.getHeader("Origin");
		if (origin != null && !origin.equals(trustedOrigin)) {
			res.sendError(HttpStatus.FORBIDDEN.value(), "Invalid Origin");
			return;
		}
		chain.doFilter(req, res);
	}
}
