package be.labil.anacarde.domain.model;

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

	@Column(nullable = false)
	@CreationTimestamp
	private LocalDateTime creationDate;

	@OneToOne
	private Producer president;
}
