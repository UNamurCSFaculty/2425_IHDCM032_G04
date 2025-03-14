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
import java.util.Arrays;
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

/**
 * Ce contrôleur fournit un point d'accès pour l'authentification des utilisateurs.
 *
 * <p>À la réussite de l'authentification, un token JWT est généré et renvoyé sous forme de cookie
 * HTTP-only.
 *
 * <p><b>Points d'accès :</b> POST /api/auth/signin : Authentifie l'utilisateur à l'aide des
 * identifiants fournis.
 */
@Tag(name = "Authentication", description = "API pour l'authentification des utilisateurs")
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final Environment environment;
    private final int tokenValidityHours;

    /**
     * Constructeur du contrôleur d'authentification.
     *
     * @param authenticationManager Le gestionnaire d'authentification pour valider les identifiants
     *     de l'utilisateur.
     * @param jwtUtil Utilitaire pour générer des tokens JWT.
     * @param environment L'environnement Spring permettant de déterminer les profils actifs.
     * @param tokenValidityHours La durée de validité du token en heures.
     */
    public AuthenticationController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            Environment environment,
            @Value("${jwt.token.validity.hours}") int tokenValidityHours) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.environment = environment;
        this.tokenValidityHours = tokenValidityHours;
    }

    /**
     * Authentifie un utilisateur et renvoie un token JWT sous forme de cookie HTTP-only.
     *
     * @param loginRequest Le payload contenant les identifiants (nom d'utilisateur et mot de
     *     passe).
     * @param response La réponse HTTP à laquelle le cookie JWT sera ajouté.
     * @return Une ResponseEntity avec un message de succès si l'authentification est réussie, ou un
     *     message d'erreur sinon.
     */
    @Operation(
            summary = "Authentifier l'utilisateur",
            description =
                    "Authentifie un utilisateur et renvoie un token JWT sous forme de cookie HTTP-only")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Utilisateur authentifié avec succès",
                content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(
                responseCode = "401",
                description = "Échec de l'authentification",
                content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(
            @Valid
                    @RequestBody
                    @Parameter(
                            description = "Identifiants de connexion",
                            required = true,
                            schema = @Schema(implementation = LoginRequest.class))
                    LoginRequest loginRequest,
            HttpServletResponse response) {
        try {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginRequest.getUsername(), loginRequest.getPassword()));

            String jwt =
                    jwtUtil.generateToken(
                            (org.springframework.security.core.userdetails.UserDetails)
                                    authentication.getPrincipal());

            // Créer un cookie HTTP-only pour améliorer la sécurité
            Cookie jwtCookie = new Cookie("jwt", jwt);
            jwtCookie.setHttpOnly(true); // Le cookie n'est pas accessible via JavaScript

            boolean isProd =
                    Arrays.stream(environment.getActiveProfiles())
                            .anyMatch(profile -> profile.equalsIgnoreCase("prod"));
            jwtCookie.setSecure(isProd);

            // Rendre le cookie accessible sur l'ensemble de l'application et définir sa durée de
            // vie
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(tokenValidityHours * 60 * 60);

            response.addCookie(jwtCookie);

            return ResponseEntity.ok("Utilisateur authentifié avec succès");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Échec de l'authentification");
        }
    }
}
