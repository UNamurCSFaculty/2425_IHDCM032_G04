package be.labil.anacarde.infrastructure.security;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Supplier;
import org.springframework.security.web.csrf.*;
import org.springframework.util.StringUtils;

/**
 * Gère le token CSRF pour SPA : - applique le masque XOR (BREACH protection) pour le rendu dans la réponse - force le
 * chargement du token pour réémettre le cookie si nécessaire - résout la valeur soit via header (plain) soit via masque
 * XOR (form)
 */
public class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

	private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
	private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
		// 1) masque BREACH
		xor.handle(request, response, csrfToken);
		// 2) force le chargement pour (ré)émission du cookie
		csrfToken.get();
	}

	@Override
	public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
		String header = request.getHeader(csrfToken.getHeaderName());
		// si on a un header, SPA JS, on dépouille sans masque
		if (StringUtils.hasText(header)) {
			return plain.resolveCsrfTokenValue(request, csrfToken);
		}
		// sinon (form server-side), on dépouille avec XOR
		return xor.resolveCsrfTokenValue(request, csrfToken);
	}
}
