package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * @brief Entity representing a User in the system.
 */
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    private Integer id;

    private String lastName;
    private String firstName;
    private String email;
    private String password;

    private LocalDateTime registrationDate;
    private LocalDateTime validationDate;
    private Boolean active;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    /**
     * @brief Retrieves the authorities granted to the user.
     *
     * This method converts the roles assigned to the user into a collection of GrantedAuthority objects.
     * If no roles are assigned, an empty list is returned.
     *
     * @return A Collection of GrantedAuthority representing the user's roles.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null) {
            return Collections.emptyList();
        }
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    /**
     * @brief Retrieves the user's password.
     *
     * @return The user's password as a String.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * @brief Retrieves the user's username, which in this implementation is the email address.
     *
     * @return The user's email address as a String.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * @brief Checks if the user's account is not expired.
     *
     * This implementation always returns true.
     *
     * @return True, indicating the account is not expired.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * @brief Checks if the user's account is not locked.
     *
     * This implementation always returns true.
     *
     * @return True, indicating the account is not locked.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * @brief Checks if the user's credentials are not expired.
     *
     * This implementation always returns true.
     *
     * @return True, indicating the credentials are not expired.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * @brief Checks if the user's account is enabled.
     *
     * This method returns the value of the 'active' field if it is not null, otherwise false.
     *
     * @return True if the account is enabled; false otherwise.
     */
    @Override
    public boolean isEnabled() {
        return active != null ? active : false;
    }

    /**
     * @brief Compares this user to the specified object for equality.
     *
     * The comparison is based on the unique identifier of the user. Special handling is included
     * to properly compare proxy instances managed by Hibernate.
     *
     * @param o The object to compare with this user.
     * @return True if the given object represents the same user; otherwise, false.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User that = (User) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    /**
     * @brief Returns the hash code for this user.
     *
     * The hash code is based on the class type, taking into account possible Hibernate proxy instances.
     *
     * @return The hash code as an integer.
     */
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}