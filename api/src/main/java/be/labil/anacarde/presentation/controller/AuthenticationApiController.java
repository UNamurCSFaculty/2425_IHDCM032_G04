package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.dto.user.UserDetailDto;
import be.labil.anacarde.domain.mapper.UserDetailMapper;
import be.labil.anacarde.domain.model.User;
import be.labil.anacarde.infrastructure.security.JwtUtil;
import be.labil.anacarde.presentation.payload.LoginRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationApiController implements AuthenticationApi {

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final Environment environment;
	private final UserDetailMapper userDetailMapper;

	@Value("${jwt.token.validity.hours}")
	private int tokenValidityHours;

	@Override
	public ResponseEntity<UserDetailDto> authenticateUser(LoginRequest loginRequest, HttpServletResponse response) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

			String jwt = jwtUtil.generateToken(
					(org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal());

			User user = (User) authentication.getPrincipal();

			Cookie jwtCookie = new Cookie("jwt", jwt);
			jwtCookie.setHttpOnly(true);
			boolean isProd = Arrays.stream(environment.getActiveProfiles())
					.anyMatch(profile -> profile.equalsIgnoreCase("prod"));
			jwtCookie.setSecure(isProd);
			jwtCookie.setPath("/");
			jwtCookie.setMaxAge(tokenValidityHours * 3600);

			response.addCookie(jwtCookie);

			UserDetailDto dto = userDetailMapper.toDto(user);

			return ResponseEntity.ok(dto);
		} catch (AuthenticationException e) {
			return ResponseEntity.status(401).build();
		}
	}

	/**
	 * Vérifie si le client est connecté : Spring Security rejettera automatiquement les requêtes non authentifiées
	 * avant d’entrer ici.
	 */
	@GetMapping("/me")
	public ResponseEntity<UserDetailDto> getCurrentUser(@AuthenticationPrincipal User currentUser) {
		// Si jamais on arrive ici sans user (extra safety), on renvoie 401
		if (currentUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		UserDetailDto dto = userDetailMapper.toDto(currentUser);
		return ResponseEntity.ok(dto);
	}

	/**
	 * Supprime le JWT HttpOnly en renvoyant un cookie vide à Max-Age=0.
	 */
	@Override
	@PostMapping("/signout")
	public ResponseEntity<Void> logout(HttpServletResponse response) {
		Cookie cleanCookie = new Cookie("jwt", "");
		cleanCookie.setHttpOnly(true);
		boolean isProd = Arrays.stream(environment.getActiveProfiles()).anyMatch(p -> p.equalsIgnoreCase("prod"));
		cleanCookie.setSecure(isProd);
		cleanCookie.setPath("/");
		cleanCookie.setMaxAge(0); // suppression immédiate
		response.addCookie(cleanCookie);

		return ResponseEntity.ok().build();
	}
}
