package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.infrastructure.security.JwtUtil;
import be.labil.anacarde.presentation.payload.LoginRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationApiController implements AuthenticationApi {

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final Environment environment;

	@Value("${jwt.token.validity.hours}")
	private int tokenValidityHours;

	@Override
	public ResponseEntity<?> authenticateUser(LoginRequest loginRequest, HttpServletResponse response) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

			String jwt = jwtUtil.generateToken(
					(org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal());

			Cookie jwtCookie = new Cookie("jwt", jwt);
			jwtCookie.setHttpOnly(true);
			boolean isProd = Arrays.stream(environment.getActiveProfiles())
					.anyMatch(profile -> profile.equalsIgnoreCase("prod"));
			jwtCookie.setSecure(isProd);
			jwtCookie.setPath("/");
			jwtCookie.setMaxAge(tokenValidityHours * 3600);

			response.addCookie(jwtCookie);
			return ResponseEntity.ok("Utilisateur authentifié avec succès");
		} catch (AuthenticationException e) {
			return ResponseEntity.status(401).body("Échec de l'authentification");
		}
	}
}
