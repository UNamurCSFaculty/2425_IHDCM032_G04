package be.labil.anacarde.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entité représentant un trader, c'est-à-dire un utilisateur pour vendre et/ou acheter.
 */
@Entity
@Table(name = "exporter")
@Getter
@Setter
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class Exporter extends Trader {
	// Aucun champ supplémentaire
}
