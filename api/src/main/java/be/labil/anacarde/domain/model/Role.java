package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
/** Entité représentant un rôle dans le système. */
public class Role extends BaseEntity {

	@Column(nullable = false, unique = true)
	private String name;
}
