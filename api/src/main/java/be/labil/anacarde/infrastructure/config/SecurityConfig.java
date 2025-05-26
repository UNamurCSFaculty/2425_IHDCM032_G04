package be.labil.anacarde.infrastructure.config;

import be.labil.anacarde.infrastructure.security.*;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Cette classe définit les beans pour les fournisseurs d'authentification, les gestionnaires
 * d'authentification, et la chaîne de filtres de sécurité. Elle configure également les politiques
 * de sécurité HTTP en désactivant CORS et CSRF, en définissant la gestion de session en mode
 * "stateless", et en configurant l'autorisation des requêtes.
 */
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
@SecurityScheme(name = "jwt", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class SecurityConfig {

	private final AuthEntryPointJwt unauthorizedHandler;
	private final AuthTokenFilter authTokenFilter;
	private final RestAccessDeniedHandler accessDeniedHandler;
	private final OriginFilter originFilter;

	@Value("${app.trusted.origin}")
	private String trustedOrigin;

	/**
	 * Cette méthode crée un bean de gestionnaire d'authentification qui est utilisé pour gérer les
	 * authentifications des utilisateurs.
	 *
	 * @param authConfig
	 *            La configuration d'authentification à utiliser pour créer le gestionnaire.
	 * @return Le gestionnaire d'authentification configuré.
	 * @throws Exception
	 *             En cas d'erreur lors de la création du gestionnaire d'authentification.
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
			throws Exception {
		return authConfig.getAuthenticationManager();
	}

	/**
	 * Cette méthode crée un bean de gestionnaire de requêtes CSRF qui est utilisé pour gérer les
	 * tokens CSRF dans les requêtes HTTP.
	 *
	 * @return Le gestionnaire de requêtes CSRF configuré.
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of(trustedOrigin));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
		src.registerCorsConfiguration("/**", config);
		return src;
	}

	/**
	 * Cette méthode crée un bean de gestionnaire de requêtes CSRF qui est utilisé pour gérer les
	 * tokens CSRF dans les requêtes HTTP.
	 *
	 * @return Le gestionnaire de requêtes CSRF configuré.
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(Customizer.withDefaults())
				.csrf(csrf -> csrf
						.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
						.csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()))
				.exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler)
						.accessDeniedHandler(accessDeniedHandler))
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.authorizeHttpRequests(auth -> auth
				// inscription « classique » et login Google
				.requestMatchers(HttpMethod.POST, "/api/users", "/api/users/google").permitAll()
				// endpoint public d’info applicative
				.requestMatchers(HttpMethod.GET, "/api/app").permitAll()
				// contact
				.requestMatchers(HttpMethod.POST, "/api/contact").permitAll()
				// auth JWT, docs, checks
				.requestMatchers("/api/auth/**", "/v3/api-docs**", "/swagger-ui/**",
						"/api/users/check/**")
				.permitAll()
				// tout le reste nécessite un JWT
				.anyRequest().authenticated());

		http.addFilterBefore(originFilter, CsrfFilter.class);
		http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
