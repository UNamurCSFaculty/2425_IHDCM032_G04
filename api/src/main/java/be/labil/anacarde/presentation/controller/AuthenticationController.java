package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.infrastructure.security.JwtUtil;
import be.labil.anacarde.presentation.payload.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * @brief Authentication Controller for handling user sign-in.
 *
 * This controller provides an endpoint for user authentication. Upon successful authentication,
 * a JWT is generated and returned as an HTTP-only cookie.
 *
 * <b>Endpoints:</b>
 * - POST /api/auth/signin: Authenticates the user with provided credentials.
 */
@Tag(name = "Authentication", description = "API for user authentication")
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final Environment environment;
    private final int tokenValidityHours;

    /**
     * @brief Constructor for AuthenticationController.
     *
     * @param authenticationManager The authentication manager to validate user credentials.
     * @param jwtUtil Utility for generating JWT tokens.
     * @param environment Spring Environment to determine active profiles.
     * @param tokenValidityHours The token validity duration in hours.
     */
    public AuthenticationController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, Environment environment,
                                    @Value("${jwt.token.validity.hours}") int tokenValidityHours) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.environment = environment;
        this.tokenValidityHours = tokenValidityHours;
    }

    /**
     * @brief Authenticates a user and returns a JWT as an HTTP-only cookie.
     *
     * @param loginRequest The login request payload containing username and password.
     * @param response HttpServletResponse to add the JWT cookie.
     * @return ResponseEntity with a success message if authentication is successful, or error message otherwise.
     *
     */
    @Operation(summary = "Authenticate user", description = "Authenticates a user and returns a JWT token as an HTTP-only cookie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User authenticated successfully", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "Failed to authenticate", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(
            @Valid @RequestBody
            @Parameter(description = "Login credentials", required = true, schema = @Schema(implementation = LoginRequest.class))
            LoginRequest loginRequest,
            HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword()
                    )
            );
            String jwt = jwtUtil.generateToken((org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal());

            Cookie jwtCookie = new Cookie("jwt", jwt);
            jwtCookie.setHttpOnly(true); // Not accessible via JavaScript

            boolean isProd = Arrays.stream(environment.getActiveProfiles())
                    .anyMatch(profile -> profile.equalsIgnoreCase("prod"));
            jwtCookie.setSecure(isProd);

            jwtCookie.setPath("/");       // Accessible across all application paths
            jwtCookie.setMaxAge(tokenValidityHours * 60 * 60);

            response.addCookie(jwtCookie);

            return ResponseEntity.ok("User authenticated successfully");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Failed to authenticate");
        }
    }
}