package be.labil.anacarde.infrastructure.persistence.view;


import be.labil.anacarde.domain.dto.db.view.ExportAuctionDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExportAuctionRepository extends Repository<ExportAuctionDto, Integer> {

    /**
     * 1) Ligne unique par ID (exemple déjà vu)
     */
    @Query(value = """
            SELECT *
            FROM   v_auction_bid_analysis
            WHERE  auction_id = :id
            """, nativeQuery = true)
    Optional<ExportAuctionDto> findByAuctionId(@Param("id") Integer id);

    /**
     * 2) Intervalle de dates + filtre « terminées » optionnel
     */
    @Query(value = """
            SELECT *
            FROM   v_auction_bid_analysis
            WHERE  auction_start_date BETWEEN :start AND :end
              AND  (:onlyEnded = false OR auction_ended = true)
            """, nativeQuery = true)
    List<ExportAuctionDto> findAllByStartDateBetween(
            @Param("start")      LocalDateTime start,
            @Param("end")        LocalDateTime end,
            @Param("onlyEnded")  boolean onlyEnded);
}
