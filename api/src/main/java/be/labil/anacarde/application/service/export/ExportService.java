package be.labil.anacarde.application.service.export;

import be.labil.anacarde.domain.dto.db.view.ExportAuctionDto;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Services de reporting basés sur la vue v_auction_bid_analysis.
 */
public interface ExportService {

	List<ExportAuctionDto> getAllAnalyses();

	/**
	 * Renvoie l’analyse complète d’une enchère.
	 *
	 * @param auctionId
	 *            id de l’enchère
	 * @return DTO projeté sur la vue
	 */
	ExportAuctionDto getAuctionById(Integer auctionId);

	/**
	 * Renvoie toutes les enchères démarrées entre {@code start} et {@code end}.
	 * 
	 * @param start
	 *            borne incluse
	 * @param end
	 *            borne incluse
	 * @param onlyEnded
	 *            {@code true} → ne garder que les enchères terminées
	 */
	List<ExportAuctionDto> getAnalysesBetween(LocalDateTime start, LocalDateTime end,
			boolean onlyEnded);
}
