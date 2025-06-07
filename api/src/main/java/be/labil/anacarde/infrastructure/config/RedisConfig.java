package be.labil.anacarde.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * Configuration Spring pour la messagerie Redis.
 * <p>
 * Définit et expose un bean {@link RedisMessageListenerContainer} permettant de souscrire à des
 * canaux Redis et de traiter les messages entrants.
 */
@Configuration
public class RedisConfig {

	/**
	 * Crée et configure un {@link RedisMessageListenerContainer}.
	 * <p>
	 * Ce conteneur est responsable de la gestion des abonnements et du dispatch des messages
	 * publiés sur les canaux Redis.
	 *
	 * @param connectionFactory
	 *            la fabrique de connexions Redis utilisée pour se connecter au serveur Redis
	 * @return un conteneur de listeners Redis prêt à être utilisé
	 */
	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(
			RedisConnectionFactory connectionFactory) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		return container;
	}
}
