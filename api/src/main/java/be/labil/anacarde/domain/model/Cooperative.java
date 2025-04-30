package be.labil.anacarde.domain.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "cooperative")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cooperative {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_cooperative")
	private Integer id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime creationDate;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "president_id", unique = true)
	@JsonManagedReference("coop-producers")
	private Producer president;
}
