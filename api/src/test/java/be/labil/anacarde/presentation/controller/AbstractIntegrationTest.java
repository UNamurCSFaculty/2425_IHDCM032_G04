package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.model.Role;
import be.labil.anacarde.domain.model.User;
import be.labil.anacarde.infrastructure.persistence.RoleRepository;
import be.labil.anacarde.infrastructure.persistence.UserRepository;
import be.labil.anacarde.infrastructure.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Classe de base pour les tests d'intégration qui nécessitent des utilisateurs et des rôles de test
 * en base de données.
 */
public abstract class AbstractIntegrationTest {

    @Autowired protected JwtUtil jwtUtil;

    @Autowired protected UserRepository userRepository;

    @Autowired protected RoleRepository roleRepository;

    @Autowired protected UserDetailsService userDetailsService;

    private User mainTestUser;
    private User secondTestUser;
    private Role userTestRole;
    private Role adminTestRole;

    @Getter private Cookie jwtCookie;

    @BeforeEach
    public void setUp() {
        initUserDatabase();
        initJwtCookie();
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    /**
     * Renvoie l'utilisateur de test principal, utilisé pour les requêtes (cookie JWT).
     *
     * @return L'utilisateur de test principal.
     * @throws IllegalStateException si l'utilisateur de test n'est pas initialisé.
     */
    public User getMainTestUser() {
        if (mainTestUser == null) {
            throw new IllegalStateException("Utilisateur de test non initialisé");
        }
        return mainTestUser;
    }

    /**
     * Renvoie le second utilisateur de test.
     *
     * @return Le second utilisateur de test.
     * @throws IllegalStateException si le second utilisateur de test n'est pas initialisé.
     */
    public User getSecondTestUser() {
        if (secondTestUser == null) {
            throw new IllegalStateException("Second utilisateur de test non initialisé");
        }
        return secondTestUser;
    }

    /**
     * Renvoie le rôle d'utilisateur de test.
     *
     * @return Le rôle d'utilisateur de test.
     * @throws IllegalStateException si le rôle d'utilisateur de test n'est pas initialisé.
     */
    public Role getUserTestRole() {
        if (userTestRole == null) {
            throw new IllegalStateException("Rôle d'utilisateur non initialisé");
        }
        return userTestRole;
    }

    /**
     * Renvoie le rôle d'administrateur de test.
     *
     * @return Le rôle d'administrateur de test.
     * @throws IllegalStateException si le rôle d'administrateur de test n'est pas initialisé.
     */
    public Role getAdminTestRole() {
        if (adminTestRole == null) {
            throw new IllegalStateException("Rôle d'administrateur non initialisé");
        }
        return adminTestRole;
    }

    /**
     * Initialise la base de données des utilisateurs avec deux utilisateurs de test et les rôles
     * associés.
     */
    public void initUserDatabase() {
        userRepository.deleteAll();

        User user1 =
                User.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .email("user@example.com")
                        .password("$2a$10$abcdefghijklmnopqrstuv1234567890AB")
                        .registrationDate(LocalDateTime.now())
                        .active(true)
                        .build();
        User user2 =
                User.builder()
                        .firstName("Foo")
                        .lastName("Bar")
                        .email("foo@bar.com")
                        .password("$2a$10$abcdefghijklmnopqrstuv1234567890AB")
                        .registrationDate(LocalDateTime.now())
                        .active(true)
                        .build();

        Role userRole = Role.builder().name("ROLE_USER").build();
        Role adminRole = Role.builder().name("ROLE_ADMIN").build();
        userTestRole = roleRepository.save(userRole);
        adminTestRole = roleRepository.save(adminRole);
        mainTestUser = userRepository.save(user1);
        secondTestUser = userRepository.save(user2);
    }

    /**
     * Génère un cookie JWT HTTP-only en utilisant les détails de l'utilisateur de test principal.
     */
    protected void initJwtCookie() {
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(getMainTestUser().getEmail());
        String token = jwtUtil.generateToken(userDetails);
        jwtCookie = new Cookie("jwt", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
    }
}
