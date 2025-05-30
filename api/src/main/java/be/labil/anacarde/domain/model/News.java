package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Entité représentant une actualité sur le blog.
 */
@Entity
@Table(name = "news")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
/** Entité représentant un article de presse dans le système. */
public class News extends BaseEntity {

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String content;

	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime creationDate;

	@Column(nullable = false)
	private LocalDateTime publicationDate;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id", nullable = false)
	private NewsCategory category;

	@Column(name = "author_name", nullable = true)
	private String authorName;
}
