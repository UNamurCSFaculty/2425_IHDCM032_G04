package be.labil.anacarde.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Supplier;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

/**
 * Gère le token CSRF pour SPA : - applique le masque XOR (BREACH protection) pour le rendu dans la
 * réponse - force le chargement du token pour réémettre le cookie si nécessaire - résout la valeur
 * soit via header (plain) soit via masque XOR (form)
 */
public class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

	private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
	private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

	/**
	 * Traite le token CSRF pour chaque requête :
	 * <ol>
	 * <li>Applique le masque XOR sur le token pour l’injecter dans la réponse (protection
	 * BREACH).</li>
	 * <li>Appelle {@code csrfToken.get()} pour forcer la lecture et (ré)émission du cookie
	 * CSRF.</li>
	 * </ol>
	 *
	 * @param request
	 *            la requête HTTP entrante
	 * @param response
	 *            la réponse HTTP pour injecter le token
	 * @param csrfToken
	 *            supplier fournissant le token CSRF courant
	 */
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			Supplier<CsrfToken> csrfToken) {
		xor.handle(request, response, csrfToken);
		csrfToken.get();
	}

	/**
	 * Résout la valeur du token CSRF provenant de la requête :
	 * <ul>
	 * <li>Si un header CSRF est présent (JavaScript SPA), utilise le plain handler.</li>
	 * <li>Sinon (formulaires traditionnels), dépouille le token masqué avec XOR.</li>
	 * </ul>
	 *
	 * @param request
	 *            la requête HTTP entrante
	 * @param csrfToken
	 *            le token CSRF à dépouiller
	 * @return la valeur du token CSRF à utiliser pour la validation
	 */
	@Override
	public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
		String header = request.getHeader(csrfToken.getHeaderName());
		if (StringUtils.hasText(header)) {
			return plain.resolveCsrfTokenValue(request, csrfToken);
		}
		return xor.resolveCsrfTokenValue(request, csrfToken);
	}
}
