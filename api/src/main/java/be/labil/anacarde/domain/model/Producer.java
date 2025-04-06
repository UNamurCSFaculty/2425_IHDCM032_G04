package be.labil.anacarde.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "producer")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
/** Entité représentant un producteur dans le système. */
public class Producer {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "producer_seq")
	@SequenceGenerator(name = "producer_seq", sequenceName = "producer_seq", allocationSize = 1)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cooperative")
	@ToString.Exclude
	private Cooperative cooperative;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_bidder", nullable = false)
	@ToString.Exclude
	private Bidder bidder;

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy
				? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
				: o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
				: this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		Producer producer = (Producer) o;
		return getId() != null && Objects.equals(getId(), producer.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}
}
