package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "cooperative")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Cooperative extends BaseEntity {

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime creationDate;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "president_id", unique = true)
	private Producer president;

	@OneToMany(mappedBy = "cooperative", fetch = FetchType.LAZY)
	@Builder.Default
	private Set<Producer> members = new HashSet<>();
}
