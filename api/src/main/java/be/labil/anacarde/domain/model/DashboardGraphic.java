package be.labil.anacarde.domain.model;

import com.google.errorprone.annotations.Immutable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@Entity
@Immutable
public class DashboardGraphic {

	@Id // clé technique : la date suffit
	@Column(name = "date")
	private LocalDateTime date;

	@Column(name = "total_open_auctions")
	private Long totalOpenAuctions;

	@Column(name = "total_new_auctions")
	private Long totalNewAuctions;
}
