package be.labil.anacarde.presentation.payload;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoginRequestTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidLoginRequest() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("user@example.com");
        loginRequest.setPassword("password");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertTrue(
                violations.isEmpty(),
                "Aucune contrainte ne devrait être violée pour un LoginRequest valide");
    }

    @Test
    public void testMissingPassword() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("user@example.com");
        loginRequest.setPassword(null);

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(
                violations.isEmpty(),
                "Il devrait y avoir une violation lorsque le mot de passe est manquant");

        boolean found = violations.stream().anyMatch(v -> v.getMessage().contains("mot de passe"));
        assertTrue(found, "La violation devrait concerner le mot de passe");
    }

    @Test
    public void testMissingUsername() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(null);
        loginRequest.setPassword("password");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(
                violations.isEmpty(),
                "Il devrait y avoir une violation lorsque le nom d'utilisateur est manquant");

        boolean found =
                violations.stream().anyMatch(v -> v.getMessage().contains("nom d'utilisateur"));
        assertTrue(found, "La violation devrait concerner le nom d'utilisateur");
    }
}
