package be.labil.anacarde.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@AllArgsConstructor
/**
 * Ce filtre intercepte les requêtes HTTP entrantes afin d'extraire et de valider les tokens JWT
 * présents dans les cookies. En cas de validation réussie, il définit l'authentification dans le
 * SecurityContext pour considérer l'utilisateur comme authentifié durant la requête en cours.
 */
public class AuthTokenFilter extends OncePerRequestFilter {

	private Environment env;
	private final JwtUtil jwtUtil;
	private final UserDetailsService userDetailsService;

	/**
	 * Cette méthode extrait le token JWT depuis la requête, le valide, et, s'il est valide, définit
	 * l'authentification dans le SecurityContext afin que l'utilisateur soit reconnu comme
	 * authentifié pour la requête actuelle.
	 *
	 * @param request
	 *            La HttpServletRequest en cours de traitement.
	 * @param response
	 *            La HttpServletResponse associée à la requête.
	 * @param filterChain
	 *            La chaîne de filtres à laquelle transmettre la requête et la réponse.
	 * @throws ServletException
	 *             en cas d'erreur liée au servlet pendant le traitement.
	 * @throws IOException
	 *             en cas d'erreur d'entrée/sortie pendant le traitement.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		try {
			String jwt = parseJwt(request);
			if (jwt != null) {
				String username = jwtUtil.extractUsername(jwt);
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				if (!userDetails.isEnabled()) {
					clearJwtCookie(response);
					throw new AuthenticationCredentialsNotFoundException(
							"Compte utilisateur désactivé");
				}
				if (!jwtUtil.validateToken(jwt, userDetails)) {
					clearJwtCookie(response);
					throw new BadCredentialsException("Token JWT invalide ou expiré");
				}
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authentication
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (AuthenticationException ex) {
			SecurityContextHolder.clearContext();
			clearJwtCookie(response);
			throw ex;
		}
		filterChain.doFilter(request, response);
	}

	/**
	 * Parcourt les cookies de la requête et retourne la valeur du cookie nommé "jwt". Si aucun
	 * cookie de ce nom n'est trouvé, retourne null.
	 *
	 * @param request
	 *            La HttpServletRequest depuis laquelle extraire le token JWT.
	 * @return Le token JWT sous forme de String s'il est présent ; sinon, null.
	 */
	private String parseJwt(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("jwt".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * Supprime le cookie JWT en envoyant un cookie avec une date d'expiration passée.
	 * 
	 * @param response
	 *            La HttpServletResponse à laquelle ajouter l'en-tête Set-Cookie.
	 */
	private void clearJwtCookie(HttpServletResponse response) {
		ResponseCookie jwtClear = ResponseCookie.from("jwt", "").httpOnly(true)
				.secure(Arrays.stream(env.getActiveProfiles())
						.anyMatch(p -> p.equalsIgnoreCase("prod")))
				.path("/").maxAge(0).sameSite("Strict").build();
		response.addHeader(HttpHeaders.SET_COOKIE, jwtClear.toString());
	}
}