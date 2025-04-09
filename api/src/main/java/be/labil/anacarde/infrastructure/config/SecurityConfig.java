package be.labil.anacarde.infrastructure.config;

import be.labil.anacarde.infrastructure.security.AuthEntryPointJwt;
import be.labil.anacarde.infrastructure.security.AuthTokenFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@AllArgsConstructor
@EnableMethodSecurity
/**
 * Cette classe définit les beans pour les fournisseurs d'authentification, les gestionnaires d'authentification, et la
 * chaîne de filtres de sécurité. Elle configure également les politiques de sécurité HTTP en désactivant CORS et CSRF,
 * en définissant la gestion de session en mode "stateless", et en configurant l'autorisation des requêtes.
 */
public class SecurityConfig {

	private final AuthEntryPointJwt unauthorizedHandler;
	private final AuthTokenFilter authTokenFilter;

	/**
	 * Retourne l'AuthenticationManager utilisé pour traiter les demandes d'authentification.
	 *
	 * @param authConfig
	 *            La configuration d'authentification contenant les détails nécessaires.
	 * @return L'instance d'AuthenticationManager.
	 * @throws Exception
	 *             en cas d'erreur lors de la récupération de l'AuthenticationManager.
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	/**
	 * Configure la chaîne de filtres de sécurité.
	 *
	 * @param http
	 *            L'instance HttpSecurity à configurer.
	 * @return La SecurityFilterChain configurée.
	 * @throws Exception
	 *             en cas d'erreur lors de la configuration.
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
				.exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
						.permitAll().anyRequest().authenticated());

		// Ajout du filtre JWT avant le filtre d'authentification standard
		http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
