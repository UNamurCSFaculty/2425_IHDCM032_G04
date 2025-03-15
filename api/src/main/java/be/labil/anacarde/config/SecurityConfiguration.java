package be.labil.anacarde.config;

import be.labil.anacarde.infrastructure.security.AuthEntryPointJwt;
import be.labil.anacarde.infrastructure.security.AuthTokenFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class SecurityConfiguration {

	private final AuthEntryPointJwt unauthorizedHandler;
	private final UserDetailsService userDetailsService;
	private final AuthTokenFilter authTokenFilter;
	private final PasswordEncoder passwordEncoder;

	/**
	 * Configure le DaoAuthenticationProvider en définissant le UserDetailsService et le PasswordEncoder à utiliser pour
	 * l'authentification des utilisateurs.
	 *
	 * @return Une instance configurée de DaoAuthenticationProvider.
	 */
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder);
		return authProvider;
	}

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
		http.cors(cors -> cors.disable()).csrf(csrf -> csrf.disable())
				.exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
						.permitAll().anyRequest().authenticated());

		http.authenticationProvider(authenticationProvider());
		http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
