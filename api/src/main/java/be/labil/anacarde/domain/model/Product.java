package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "product")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
	private Integer id;
}
