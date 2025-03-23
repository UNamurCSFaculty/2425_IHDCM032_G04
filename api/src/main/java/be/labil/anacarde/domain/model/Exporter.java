package be.labil.anacarde.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "exporter")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
/** Entité représentant un exportateur. */
public class Exporter extends Buyer {

}