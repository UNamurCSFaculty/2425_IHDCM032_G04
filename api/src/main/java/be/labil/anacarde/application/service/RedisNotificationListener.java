package be.labil.anacarde.application.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

/**
 * Définit le contrat pour un écouteur de notifications Redis.
 * <p>
 * Implémentations de cette interface doivent s’abonner aux canaux Redis
 * afin de recevoir et traiter les événements publiés.
 */
public interface RedisNotificationListener {

	/**
	 * Méthode appelée automatiquement après l’initialisation du bean.
	 * <p>
	 * Doit contenir la logique d’abonnement aux canaux Redis (par exemple
	 * via un {@code MessageListener} ou un template Redis).
	 */
	@PostConstruct
	void subscribe();

	/**
	 * Représente un événement de notification reçu depuis Redis.
	 * <p>
	 * Contient le nom du canal ou type d’événement, ainsi que les données
	 * associées au message.
	 */
	@Setter
	@Getter
	public static class RedisNotificationEvent {
		private String eventName;
		private Object data;

	}
}
