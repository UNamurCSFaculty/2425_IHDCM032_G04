package be.labil.anacarde.application.service;

import java.util.Set;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Service pour gérer les abonnements et les événements SSE (Server-Sent Events) pour les enchères.
 */
public interface AuctionSseService {

	/**
	 * Abonne un utilisateur à une enchère spécifique.
	 *
	 * @param auctionId
	 *            l'ID de l'enchère
	 * @param userKey
	 *            la clé de l'utilisateur
	 * @return un SseEmitter pour envoyer des événements à l'utilisateur
	 */
	SseEmitter subscribe(Integer auctionId, String userKey, boolean isVisitor);

	/**
	 * Supprime l'abonnement d'un utilisateur à une enchère.
	 *
	 * @param auctionId
	 *            l'ID de l'enchère
	 * @param userKey
	 *            la clé de l'utilisateur à supprimer de l'abonnement
	 */
	void removeVisitor(Integer auctionId, String userKey);

	/**
	 * Supprime tous les abonnements d'un utilisateur.
	 *
	 * @param userKey
	 *            la clé de l'utilisateur dont les abonnements doivent être supprimés
	 */
	void removeSubscriber(Integer auctionId, String userKey);

	/**
	 * Envoie un événement à tous les abonnés d'une enchère.
	 *
	 * @param auctionId
	 *            l'ID de l'enchère
	 * @param eventName
	 *            le nom de l'événement
	 * @param data
	 *            les données à envoyer avec l'événement
	 */
	void sendEvent(Integer auctionId, String eventName, Object data);

	/**
	 * Récupère les visiteurs d'une enchère spécifique.
	 *
	 * @param auctionId
	 *            l'ID de l'enchère
	 * @return un ensemble de clés d'utilisateurs visiteurs de l'enchère
	 */
	Set<String> getVisitors(Integer auctionId);

	/**
	 * Récupère les abonnés d'une enchère spécifique.
	 *
	 * @param auctionId
	 *            l'ID de l'enchère
	 * @return un ensemble de clés d'utilisateurs abonnés à l'enchère
	 */
	Set<String> getSubscribers(Integer auctionId);

	/**
	 * Ajoute un utilisateur comme abonné à une enchère.
	 *
	 * @param auctionId
	 *            l'ID de l'enchère
	 * @param userKey
	 *            la clé de l'utilisateur à ajouter comme abonné
	 */
	void addSubscriber(Integer auctionId, String userKey);
}
