package be.labil.anacarde.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Entité représentant un administrateur de la plateforme.
 */
@Entity
@Table(name = "admin")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class Admin extends User {
}