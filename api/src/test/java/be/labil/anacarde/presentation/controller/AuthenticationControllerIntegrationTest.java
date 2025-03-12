package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.model.User;
import be.labil.anacarde.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUpDatabase() {
        userRepository.deleteAll();

        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("user@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRegistrationDate(LocalDateTime.now());
        user.setActive(true);

        userRepository.save(user);
    }

    @Test
    public void testAuthenticateUserIntegration_Success() throws Exception {
        String requestBody = "{\"username\": \"user@example.com\", \"password\": \"password\"}";

        mockMvc.perform(post("/api/auth/signin")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("jwt"))
                .andExpect(content().string("User authenticated successfully"));
    }

    @Test
    public void testAuthenticateUserIntegration_Failure() throws Exception {
        String requestBody = "{\"username\": \"user@example.com\", \"password\": \"wrongpassword\"}";

        mockMvc.perform(post("/api/auth/signin")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Failed to authenticate"));
    }
}