package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.dto.user.UserDetailDto;
import be.labil.anacarde.domain.mapper.UserDetailMapper;
import be.labil.anacarde.domain.model.User;
import be.labil.anacarde.infrastructure.security.JwtUtil;
import be.labil.anacarde.presentation.payload.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationApiController implements AuthenticationApi {

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final Environment environment;
	private final UserDetailMapper userDetailMapper;

	@Value("${jwt.token.validity.months}")
	private int tokenValidityMonths;

	@Override
	public ResponseEntity<UserDetailDto> authenticateUser(LoginRequest loginRequest, HttpServletResponse response) {
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		String jwt = jwtUtil
				.generateToken((org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal());

		boolean isProd = Arrays.stream(environment.getActiveProfiles()).anyMatch(p -> p.equalsIgnoreCase("prod"));

		ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwt).httpOnly(true).secure(isProd).path("/")
				.maxAge(Duration.ofDays(tokenValidityMonths)).sameSite("Strict").build();
		response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

		UserDetailDto dto = userDetailMapper.toDto((User) auth.getPrincipal());
		return ResponseEntity.ok(dto);
	}

	@Override
	public ResponseEntity<UserDetailDto> getCurrentUser(@AuthenticationPrincipal User currentUser,
			HttpServletRequest request, HttpServletResponse response) {
		if (currentUser == null) {
			throw new AuthenticationCredentialsNotFoundException("Current user is null");
		}
		// Le cookie XSRF-TOKEN a déjà été émis par le filtre Spring CSRF
		UserDetailDto dto = userDetailMapper.toDto(currentUser);
		return ResponseEntity.ok(dto);
	}

	@Override
	public ResponseEntity<Void> logout(HttpServletResponse response) {
		ResponseCookie jwtClear = ResponseCookie.from("jwt", "").httpOnly(true)
				.secure(Arrays.stream(environment.getActiveProfiles()).anyMatch(p -> p.equalsIgnoreCase("prod")))
				.path("/").maxAge(0).sameSite("Strict").build();
		response.addHeader(HttpHeaders.SET_COOKIE, jwtClear.toString());
		return ResponseEntity.ok().build();
	}
}
