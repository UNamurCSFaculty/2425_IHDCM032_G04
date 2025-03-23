package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "season")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
/** Entité représentant une saison agricole. */
public class Season {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "season_seq")
	@SequenceGenerator(name = "season_seq", sequenceName = "season_seq", allocationSize = 1)
	private Integer id;

	@Column(name = "start_date", nullable = false)
	private LocalDateTime startDate;

	@Column(name = "end_date", nullable = false)
	private LocalDateTime endDate;
}
