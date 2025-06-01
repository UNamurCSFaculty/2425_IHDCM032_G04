package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Entité représentant un utilisateur dans le système.
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class User extends BaseEntity implements UserDetails {

	@Column(nullable = false)
	private String lastName;

	@Column(nullable = false)
	private String firstName;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime registrationDate;

	private LocalDateTime validationDate;

	@Column(nullable = false)
	private boolean enabled;

	@Embedded
	private Address address;

	@Column(nullable = false, unique = true)
	private String phone;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Language language;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@Builder.Default
	private List<Document> documents = new ArrayList<>();

	/**
	 * Convertit le type d'utilisateur en autorités basées sur l'héritage de classe. Les Admin ont
	 * ROLE_ADMIN et ROLE_USER, les autres utilisateurs n'ont que ROLE_USER.
	 *
	 * @return Une Collection de GrantedAuthority basée sur le type d'utilisateur.
	 */
	@Override
	@Transient
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

		if (this instanceof Trader) {
			authorities.add(new SimpleGrantedAuthority("ROLE_TRADER"));
		}

		if (this instanceof Admin) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		} else if (this instanceof QualityInspector) {
			authorities.add(new SimpleGrantedAuthority("ROLE_QUALITY_INSPECTOR"));
		} else if (this instanceof Producer) {
			authorities.add(new SimpleGrantedAuthority("ROLE_PRODUCER"));
			authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
		} else if (this instanceof Exporter) {
			authorities.add(new SimpleGrantedAuthority("ROLE_EXPORTER"));
			authorities.add(new SimpleGrantedAuthority("ROLE_BUYER"));
		} else if (this instanceof Carrier) {
			authorities.add(new SimpleGrantedAuthority("ROLE_CARRIER"));
		} else if (this instanceof Transformer) {
			authorities.add(new SimpleGrantedAuthority("ROLE_TRANSFORMER"));
			authorities.add(new SimpleGrantedAuthority("ROLE_BUYER"));
			authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
		}

		return authorities;
	}

	/**
	 * Récupère le mot de passe de l'utilisateur.
	 *
	 * @return Le mot de passe de l'utilisateur sous forme de String.
	 */
	@Override
	public String getPassword() {
		return password;
	}

	/**
	 * Récupère le nom d'utilisateur de l'utilisateur, qui dans cette implémentation correspond à
	 * l'adresse e-mail.
	 *
	 * @return L'adresse e-mail de l'utilisateur sous forme de String.
	 */
	@Override
	@Transient
	public String getUsername() {
		return email;
	}

	/**
	 * Cette implémentation retourne toujours true.
	 *
	 * @return true, indiquant que le compte n'est pas expiré.
	 */
	@Override
	@Transient
	public boolean isAccountNonExpired() {
		return true;
	}

	/**
	 * Cette implémentation retourne toujours true.
	 *
	 * @return true, indiquant que le compte n'est pas verrouillé.
	 */
	@Override
	@Transient
	public boolean isAccountNonLocked() {
		return true;
	}

	/**
	 * Cette implémentation retourne toujours true.
	 *
	 * @return true, indiquant que les identifiants ne sont pas expirés.
	 */
	@Override
	@Transient
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/**
	 * Retourne la valeur du champ 'active'.
	 *
	 * @return true si le compte est activé ; false sinon.
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
}
