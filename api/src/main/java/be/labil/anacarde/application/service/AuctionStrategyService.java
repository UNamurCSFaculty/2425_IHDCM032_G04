package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.AuctionStrategyDto;
import java.util.List;

/**
 * Ce service offre des méthodes permettant de créer, récupérer, mettre à jour et supprimer des stratégies d'enchère.
 */
public interface AuctionStrategyService {

	/**
	 * Crée une nouvelle stratégie d'enchère dans le système à partir du DTO fourni.
	 *
	 * @param auctionStrategyDto
	 *            Le DTO contenant les informations de la stratégie à créer.
	 * @return Le DTO représentant la stratégie créée.
	 */
	AuctionStrategyDto createAuctionStrategy(AuctionStrategyDto auctionStrategyDto);

	/**
	 * Retourne une stratégie d'enchère correspondant à l'ID fourni.
	 *
	 * @param id
	 *            L'identifiant unique de la stratégie.
	 * @return Le DTO représentant la stratégie trouvée.
	 */
	AuctionStrategyDto getAuctionStrategyById(Integer id);

	/**
	 * Récupère toutes les stratégies d'enchère disponibles dans le système.
	 *
	 * @return Une liste de DTO représentant toutes les stratégies.
	 */
	List<AuctionStrategyDto> listAuctionStrategies();

	/**
	 * Met à jour une stratégie d'enchère identifiée par l'ID donné avec les informations fournies.
	 *
	 * @param id
	 *            L'identifiant de la stratégie à mettre à jour.
	 * @param auctionStrategyDto
	 *            Le DTO contenant les nouvelles informations.
	 * @return Le DTO représentant la stratégie mise à jour.
	 */
	AuctionStrategyDto updateAuctionStrategy(Integer id, AuctionStrategyDto auctionStrategyDto);

	/**
	 * Supprime une stratégie d'enchère identifiée par l'ID fourni.
	 *
	 * @param id
	 *            L'identifiant de la stratégie à supprimer.
	 */
	void deleteAuctionStrategy(Integer id);
}
