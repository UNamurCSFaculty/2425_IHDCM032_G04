package be.labil.anacarde.application.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

/**
 * Cette interface définit un écouteur pour les notifications Redis. Elle est utilisée pour recevoir
 * des événements de notification
 */
public interface RedisNotificationListener {

	/**
	 * Méthode pour s'abonner aux notifications Redis. Elle est appelée après la construction du
	 * bean.
	 */
	@PostConstruct
	void subscribe();

	/**
	 * Classe représentant un événement de notification Redis. Elle contient le nom de l'événement
	 * et les données associées.
	 */
	@Setter
	@Getter
	public static class RedisNotificationEvent {
		private String eventName;
		private Object data;

	}
}
