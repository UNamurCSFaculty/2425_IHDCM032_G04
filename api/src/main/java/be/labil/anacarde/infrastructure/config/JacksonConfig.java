package be.labil.anacarde.infrastructure.config;

import be.labil.anacarde.infrastructure.config.jackson.JtsModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Jackson pour l’application.
 * <p>
 * Permet d’ajouter des modules personnalisés au {@code ObjectMapper} de Spring, notamment le module
 * JTS pour la (dé)sérialisation des géométries.
 */
@Configuration
public class JacksonConfig {

	/**
	 * Bean permettant de personnaliser le constructeur Jackson de Spring Boot afin d’enregistrer le
	 * {@link JtsModule}.
	 * <p>
	 * Le module JTS ajoute le support de (dé)sérialisation pour les types géométriques JTS (par ex.
	 * {@code Point}).
	 *
	 * @return un {@link Jackson2ObjectMapperBuilderCustomizer} qui installe le JtsModule
	 */
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer addJtsModule() {
		return builder -> builder.modulesToInstall(new JtsModule());
	}
}