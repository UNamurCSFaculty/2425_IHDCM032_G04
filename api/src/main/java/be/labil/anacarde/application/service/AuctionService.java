
package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.db.AuctionDto;
import be.labil.anacarde.domain.dto.write.AuctionUpdateDto;
import java.util.List;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Ce service offre des méthodes permettant de créer, récupérer, mettre à jour et supprimer des
 * informations d'enchères.
 */
public interface AuctionService {

	/**
	 * Crée une nouvelle enchère dans le système en utilisant le AuctionDto fourni.
	 *
	 * @param auctionDto
	 *            Le AuctionDto contenant les informations de la nouvelle enchère.
	 * @return Un AuctionDto représentant l'enchère créée.
	 */
	@PreAuthorize("@authz.isAdmin(principal) or (#auctionDto.traderId == principal.id)")
	AuctionDto createAuction(@Param("auctionDto") AuctionUpdateDto auctionDto);

	/**
	 * Retourne l'enchère correspondant à l'ID fourni.
	 *
	 * @param auctionId
	 *            L'identifiant unique de l'enchère.
	 * @return Un AuctionDto représentant l'enchère avec l'ID spécifié.
	 */
	AuctionDto getAuctionById(Integer auctionId);

	/**
	 * Récupère toutes les enchères du système.
	 *
	 * @param traderId
	 *            L'identifiant du trader ayant créé les enchères.
	 * @param buyerId
	 *            L'identifiant du trader ayant remporté les enchères.
	 * @param auctionStatus
	 *            Le status de l'enchère.
	 *
	 * @return Une List de AuctionDto représentant toutes les enchères.
	 */
	List<AuctionDto> listAuctions(Integer traderId, Integer buyerId, String auctionStatus);

	/**
	 * Mise à jour de l'enchère identifiée par l'ID donné avec les informations fournies dans le
	 * AuctionDto.
	 *
	 * @param auctionId
	 *            L'identifiant unique de l'enchère à mettre à jour.
	 * @param auctionDto
	 *            Le AuctionDto contenant les informations mises à jour.
	 * @return Un AuctionDto représentant l'enchère mis à jour.
	 */
	@PreAuthorize("@authz.isAdmin(principal) or (#auctionDto.traderId == principal.id)")
	AuctionDto updateAuction(Integer auctionId, @Param("auctionDto") AuctionUpdateDto auctionDto);

	/**
	 * Accepter l'enchère identifiée par l'ID donné.
	 *
	 * @param auctionId
	 *            L'identifiant unique de l'enchère à mettre à jour.
	 * @return Un AuctionDto représentant l'enchère mis à jour.
	 */
	@PreAuthorize("@authz.isAdmin(principal) or (@ownership.isAuctionOwner(principal.id, #auctionId))")
	AuctionDto acceptAuction(@Param("auctionId") Integer auctionId);

	/**
	 * Supprime l'enchère identifiée par l'ID donné du système.
	 *
	 * @param auctionId
	 *            L'identifiant unique de l'enchère à supprimer.
	 */
	@PreAuthorize("@authz.isAdmin(principal) or (@ownership.isAuctionOwner(principal.id, #auctionId))")
	void deleteAuction(@Param("auctionId") Integer auctionId);

	/**
	 * Clôture l'enchère identifiée par l'ID donné
	 *
	 * @param auctionId
	 *            L'identifiant unique de l'enchère à clôturer.
	 */
	void closeAuction(Integer auctionId);
}
