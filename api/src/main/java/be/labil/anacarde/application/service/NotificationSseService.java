package be.labil.anacarde.application.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Service pour gérer les abonnements et les événements SSE (Server-Sent Events) pour les
 * notifications.
 */
public interface NotificationSseService {

	/**
	 * Abonne un utilisateur pour recevoir des événements SSE.
	 *
	 * @param userKey
	 *            la clé de l'utilisateur
	 * @return un SseEmitter pour envoyer des événements à l'utilisateur
	 */
	SseEmitter subscribe(String userKey);

	/**
	 * Envoie un événement à un utilisateur spécifique.
	 *
	 * @param userKey
	 *            la clé de l'utilisateur
	 * @param eventName
	 *            le nom de l'événement
	 * @param data
	 *            les données à envoyer avec l'événement
	 */
	void sendEvent(String userKey, String eventName, Object data);

	/**
	 * Publie un événement pour un utilisateur spécifique.
	 *
	 * @param userKey
	 *            la clé de l'utilisateur
	 * @param eventName
	 *            le nom de l'événement
	 * @param data
	 *            les données à envoyer avec l'événement
	 */
	void publishEvent(String userKey, String eventName, Object data);
}
