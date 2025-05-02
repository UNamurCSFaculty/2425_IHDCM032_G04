package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "document", indexes = {@Index(columnList = "user_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
/** Entité représentant un document dans le système. */
public class Document extends BaseEntity {

	@Column(nullable = false)
	private String type;

	@Column(nullable = false)
	private String format;

	@Column(nullable = false)
	private String storagePath;

	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime uploadDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
}
