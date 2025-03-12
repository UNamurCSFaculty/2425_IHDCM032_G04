package be.labil.anacarde.infrastructure.security;

import be.labil.anacarde.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtil();
        // Inject configuration values using ReflectionTestUtils for testing purposes
        ReflectionTestUtils.setField(jwtUtil, "secretKey", "V2Vha1NlY3VyZUtleVNlY3VyZUtleVNlY3VyZUtleVNlY3VyZQ==");
        ReflectionTestUtils.setField(jwtUtil, "tokenValidityHours", 1);
    }

    @Test
    public void testGenerateAndValidateToken() {
        // Create a dummy user with no authorities
        User user = new User();
        user.setEmail("myUser");
        user.setPassword("password");

        // Generate a token for the user
        String token = jwtUtil.generateToken(user);
        assertNotNull(token, "Token should not be null");

        // Extract the username from the generated token
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(user.getUsername(), extractedUsername, "Extracted username should match the original");

        // Validate the token and ensure it is valid for the given user details
        boolean isTokenValid = jwtUtil.validateToken(token, user);
        assertTrue(isTokenValid, "Token should be valid");
    }

    @Test
    public void testTokenExpiration() throws InterruptedException {
        // For testing expiration, we set token validity to 0 hours (immediate expiration)
        ReflectionTestUtils.setField(jwtUtil, "tokenValidityHours", 0L);
        User user = new User();
        user.setEmail("myUser");
        user.setPassword("password");

        String token = jwtUtil.generateToken(user);

        // Wait a short period to ensure the token expires
        Thread.sleep(2);

        boolean isTokenValid = jwtUtil.validateToken(token, user);
        assertFalse(isTokenValid, "Token should be expired");
    }
}
