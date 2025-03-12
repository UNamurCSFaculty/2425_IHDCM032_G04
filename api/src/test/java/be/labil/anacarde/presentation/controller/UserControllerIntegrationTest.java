package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.dto.UserDto;
import be.labil.anacarde.domain.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for UserController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * RequestPostProcessor to automatically add the JWT cookie to each request.
     */
    private RequestPostProcessor jwt() {
        return request -> {
            request.setCookies(getJwtCookie());
            return request;
        };
    }

    @Test
    public void testGetUser() throws Exception {
        mockMvc.perform(get("/api/users/" + getMainTestUser().getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(getMainTestUser().getEmail()));
    }

    @Test
    public void testCreateUser() throws Exception {
        UserDto newUser = new UserDto();
        newUser.setFirstName("Alice");
        newUser.setLastName("Smith");
        newUser.setEmail("alice.smith@example.com");
        newUser.setPassword("secret");

        ObjectNode node = objectMapper.valueToTree(newUser);
        node.put("password", newUser.getPassword()); // Add manually because password is not serialized
        String jsonContent = node.toString();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .with(jwt()))
                .andExpect(status().isCreated())
                // Check that the Location header contains the new user's ID
                .andExpect(header().string("Location", containsString("/api/users/")))
                .andExpect(jsonPath("$.email").value("alice.smith@example.com"))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Smith"));

        // Check that the password is hashed correctly
        User createdUser = userRepository.findByEmail("alice.smith@example.com")
                .orElseThrow(() -> new AssertionError("User not found"));
        assertTrue(bCryptPasswordEncoder.matches("secret", createdUser.getPassword()),
                "The stored password should match the raw password 'secret'");
    }

    @Test
    public void testListUsers() throws Exception {
        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testUpdateUser() throws Exception {
        UserDto updateUser = new UserDto();
        updateUser.setFirstName("John Updated");
        updateUser.setLastName("Doe Updated");
        updateUser.setEmail("email@updated.com");
        updateUser.setPassword("newpassword");

        ObjectNode node = objectMapper.valueToTree(updateUser);
        node.put("password", updateUser.getPassword()); // Add manually because password is not serialized
        String jsonContent = node.toString();

        mockMvc.perform(put("/api/users/" + getMainTestUser().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John Updated"));

        // Verify that the password is hashed correctly after update
        User updatedUser = userRepository.findByEmail("email@updated.com")
                .orElseThrow(() -> new AssertionError("User not found"));
        assertTrue(bCryptPasswordEncoder.matches("newpassword", updatedUser.getPassword()),
                "The stored password should match the updated password 'newpassword'");
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/" + getSecondTestUser().getId())
                        .with(jwt()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/" + getSecondTestUser().getId())
                        .with(jwt()))
                .andExpect(status().isNotFound());
    }
}
