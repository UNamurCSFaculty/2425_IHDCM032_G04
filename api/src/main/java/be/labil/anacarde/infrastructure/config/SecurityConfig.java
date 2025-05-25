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

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
@SecurityScheme(name = "jwt", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
/**
 * Cette classe définit les beans pour les fournisseurs d'authentification, les gestionnaires
 * d'authentification, et la chaîne de filtres de sécurité. Elle configure également les politiques
 * de sécurité HTTP en désactivant CORS et CSRF, en définissant la gestion de session en mode
 * "stateless", et en configurant l'autorisation des requêtes.
 */
public class SecurityConfig {

	private final AuthEntryPointJwt unauthorizedHandler;
	private final AuthTokenFilter authTokenFilter;
	private final RestAccessDeniedHandler accessDeniedHandler;
	private final OriginFilter originFilter;

	@Value("${app.trusted.origin}")
	private String trustedOrigin;

	@Bean
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
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
			throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	/**
	 * Cette méthode crée un bean de gestionnaire de requêtes CSRF qui est utilisé pour gérer les
	 * tokens CSRF dans les requêtes HTTP.
	 *
	 * @return Le gestionnaire de requêtes CSRF configuré.
	 */
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

	@Bean
	/**
	 * Cette méthode crée un bean de gestionnaire de requêtes CSRF qui est utilisé pour gérer les
	 * tokens CSRF dans les requêtes HTTP.
	 *
	 * @return Le gestionnaire de requêtes CSRF configuré.
	 */
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// On utilise CookieCsrfTokenRepository + SpaHandler
		http.cors(Customizer.withDefaults())
				.csrf(csrf -> csrf
						.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
						.csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()))
				.exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler)
						.accessDeniedHandler(accessDeniedHandler))
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// Autorisations
		http.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.POST, "/api/users")
				.permitAll().requestMatchers(HttpMethod.GET, "/api/app").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/contact").permitAll()
				.requestMatchers("/api/auth/**", "/v3/api-docs**", "/swagger-ui/**",
						"/api/users/check/**")
				.permitAll().anyRequest().authenticated());

		// Filtres
		http.addFilterBefore(originFilter, CsrfFilter.class);
		http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
