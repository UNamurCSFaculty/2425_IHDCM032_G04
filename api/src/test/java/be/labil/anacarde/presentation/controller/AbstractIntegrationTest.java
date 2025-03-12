package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.model.User;
import be.labil.anacarde.infrastructure.persistence.UserRepository;
import be.labil.anacarde.infrastructure.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public abstract class AbstractIntegrationTest {

    @Autowired protected JwtUtil jwtUtil;

    @Autowired protected UserRepository userRepository;

    @Autowired protected UserDetailsService userDetailsService;

    private User mainTestUser;
    private User secondTestUser;

    @Getter private Cookie jwtCookie;

    @BeforeEach
    public void setUp() {
        initUserDatabase();
        initJwtCookie();
    }

    /** Returns the main test user, the one that is used for the requests (JWT cookie). */
    public User getMainTestUser() {
        if (mainTestUser == null) {
            throw new IllegalStateException("Test user not initialized");
        }
        return mainTestUser;
    }

    /** Returns the second test user. */
    public User getSecondTestUser() {
        if (secondTestUser == null) {
            throw new IllegalStateException("Second test user not initialized");
        }
        return secondTestUser;
    }

    /** Initializes the user database with a single test user. */
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
        mainTestUser = userRepository.save(user1);
        secondTestUser = userRepository.save(user2);
    }

    /** Generates a HttpOnly JWT cookie using the test user's details. */
    protected void initJwtCookie() {
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(getMainTestUser().getEmail());
        String token = jwtUtil.generateToken(userDetails);
        jwtCookie = new Cookie("jwt", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
    }
}
