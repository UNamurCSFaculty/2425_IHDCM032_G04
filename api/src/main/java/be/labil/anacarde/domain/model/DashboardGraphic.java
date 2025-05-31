package be.labil.anacarde.domain.model;

import com.google.errorprone.annotations.Immutable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;

/**
 * Entité représentant les données affichées de manière graphique dans le tableau de bord admin.
 * ENTITÉ STRICTEMENT EN LECTURE : aucune opération INSERT/UPDATE/DELETE.
 */
@Getter
@Entity
@Immutable
public class DashboardGraphic {

	@Id
	@Column(name = "date")
	private LocalDateTime date;

	@Column(name = "total_open_auctions")
	private Long totalOpenAuctions;

	@Column(name = "total_new_auctions")
	private Long totalNewAuctions;
}
