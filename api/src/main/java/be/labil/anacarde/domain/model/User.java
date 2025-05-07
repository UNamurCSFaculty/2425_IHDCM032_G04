package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
/** Entité représentant un utilisateur dans le système. */
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

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles;

	private String address;

	@Column(nullable = false, unique = true)
	private String phone;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Language language;

	/**
	 * Ajoute un rôle à cet utilisateur et met à jour le côté inverse (le rôle) pour maintenir la cohérence.
	 *
	 * @param role
	 *            Le rôle à ajouter.
	 */
	public void addRole(Role role) {
		if (role != null) {
			if (roles == null) {
				roles = new HashSet<>();
			}
			roles.add(role);
		}
	}

	/**
	 * Convertit les rôles assignés à l'utilisateur en une collection d'objets GrantedAuthority. Si aucun rôle n'est
	 * assigné, une liste vide est retournée.
	 *
	 * @return Une Collection de GrantedAuthority représentant les rôles de l'utilisateur.
	 */
	@Override
	@Transient
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (roles == null) {
			return new ArrayList<>();
		}
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}

	/**
	 * Retourne les rôles de l'utilisateur.
	 *
	 * @return Un ensemble vide si l'utilisateur n'a aucun rôle ; sinon, les rôles de l'utilisateur.
	 */
	public Set<Role> getRoles() {
		if (roles == null) {
			return new HashSet<>();
		}
		return roles;
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
	 * Récupère le nom d'utilisateur de l'utilisateur, qui dans cette implémentation correspond à l'adresse e-mail.
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
