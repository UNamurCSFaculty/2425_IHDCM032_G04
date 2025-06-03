package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.NotificationSseServiceImpl;
import be.labil.anacarde.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class NotificationSseApiController implements NotificationSseApi {
	private static final Logger log = LoggerFactory.getLogger(NotificationSseApiController.class);

	private final NotificationSseServiceImpl notificationSseService;
	private final JwtUtil jwtUtil;

	@GetMapping("/stream")
	@Override
	public SseEmitter subscribe(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(value = "token", required = false) String token) {
		String userKey = null;
		if (token != null && !token.isBlank()) {
			log.info("[SSE] Token.");
			try {
				userKey = jwtUtil.extractUsername(token);
			} catch (Exception e) {
				log.warn("[SSE] Invalid JWT token for SSE: {}", e.getMessage());
				throw new RuntimeException("Token JWT invalide pour SSE", e);
			}
		} else if (userDetails != null) {
			userKey = userDetails.getUsername();
			log.info("[SSE] UserDetails: {}", userKey);
		}
		if (userKey == null) {
			log.warn("[SSE] Unauthentified User");
			throw new RuntimeException("Utilisateur non authentifié pour SSE");
		}
		log.info("[SSE] Subscription OK for userKey: {}", userKey);
		return notificationSseService.subscribe(userKey);
	}
}
