package be.labil.anacarde.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "translation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Translation extends BaseEntity {

	@Column(name = "translation_key", nullable = false, unique = true)
	private String key;
}