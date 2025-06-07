package be.labil.anacarde.infrastructure.datafaker;

import net.datafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration pour le générateur de données factices.
 * <p>
 * Définit un bean {@link Faker} utilisable dans l’application pour peupler la base de données avec
 * des données aléatoires.
 */
@Configuration
public class FakerConfig {

	/**
	 * Crée et expose un bean {@link Faker}.
	 *
	 * @return une instance de {@code Faker} prête à générer des données factices
	 */
	@Bean
	public Faker faker() {
		return new Faker();
	}
}
