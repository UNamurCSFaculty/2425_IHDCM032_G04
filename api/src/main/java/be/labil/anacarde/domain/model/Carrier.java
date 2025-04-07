package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "carrier")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class Carrier extends User {

	@Column(nullable = false)
	private BigDecimal pricePerKm;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "carrier_region", joinColumns = @JoinColumn(name = "carrier_id"), inverseJoinColumns = @JoinColumn(name = "region_id"))
	private Set<Region> regions;

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
		Carrier that = (Carrier) o;
		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}
}