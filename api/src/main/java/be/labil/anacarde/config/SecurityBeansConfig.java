package be.labil.anacarde.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
/**
 * Cette classe fournit les définitions de beans liés à la sécurité nécessaires à l'application. Elle inclut notamment
 * la définition d'un bean PasswordEncoder qui permet de chiffrer les mots de passe en utilisant l'algorithme BCrypt.
 */
public class SecurityBeansConfig {

	/**
	 * Instancie et retourne un BCryptPasswordEncoder utilisé pour encoder les mots de passe des utilisateurs.
	 *
	 * @return Une instance de PasswordEncoder basée sur l'algorithme BCrypt.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
