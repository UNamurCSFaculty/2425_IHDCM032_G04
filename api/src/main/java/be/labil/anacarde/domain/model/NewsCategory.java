package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "category", indexes = {@Index(name = "idx_category_name", columnList = "name")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
/** Entité représentant une catégorie dans le système. */
public class NewsCategory extends BaseEntity {

	@Column(nullable = false, unique = true)
	private String name;
}
