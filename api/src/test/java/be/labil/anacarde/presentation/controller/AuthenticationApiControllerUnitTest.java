package be.labil.anacarde.presentation.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import be.labil.anacarde.infrastructure.security.JwtUtil;
import be.labil.anacarde.presentation.payload.LoginRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

/** Test unitaire du contrôleur d'authentification. */
@ExtendWith(MockitoExtension.class)
public class AuthenticationApiControllerUnitTest {

    @Mock private AuthenticationManager authenticationManager;

    @Mock private Environment environment;

    @Mock private JwtUtil jwtUtil;

    @Mock private HttpServletResponse httpServletResponse;

    private AuthenticationApiController authenticationController;

    @BeforeEach
    public void setUp() {
        // Simuler le profil actif "test"
        Mockito.lenient().when(environment.getActiveProfiles()).thenReturn(new String[] {"test"});
        authenticationController =
                new AuthenticationApiController(authenticationManager, jwtUtil, environment);
        ReflectionTestUtils.setField(authenticationController, "tokenValidityHours", 5);
    }

    /** Teste le scénario d'authentification réussi. */
    @Test
    public void testAuthenticateUser_Success() {
        String username = "user@example.com";
        String password = "password";
        String jwtToken = "test-jwt-token";

        UserDetails userDetails = new User(username, password, new ArrayList<>());
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails, password, userDetails.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(authToken);
        when(jwtUtil.generateToken(any())).thenReturn(jwtToken);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        ResponseEntity<?> responseEntity =
                authenticationController.authenticateUser(loginRequest, httpServletResponse);

        verify(httpServletResponse, times(1)).addCookie(cookieCaptor.capture());
        Cookie jwtCookie = cookieCaptor.getValue();
        assertEquals("jwt", jwtCookie.getName());
        assertEquals(jwtToken, jwtCookie.getValue());

        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals("Utilisateur authentifié avec succès", responseEntity.getBody());
    }

    /** Teste le scénario d'échec d'authentification. */
    @Test
    public void testAuthenticateUser_Failure() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Identifiants invalides"));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("user@example.com");
        loginRequest.setPassword("wrongpassword");

        ResponseEntity<?> responseEntity =
                authenticationController.authenticateUser(loginRequest, httpServletResponse);

        assertEquals(401, responseEntity.getStatusCode().value());
        assertEquals("Échec de l'authentification", responseEntity.getBody());
    }
}
