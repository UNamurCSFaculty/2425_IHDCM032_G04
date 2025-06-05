package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.AuctionSseServiceImpl;
import be.labil.anacarde.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class AuctionSseApiController implements AuctionSseApi {
	private static final Logger log = LoggerFactory.getLogger(AuctionSseApiController.class);
	private final AuctionSseServiceImpl auctionSseService;
	private final JwtUtil jwtUtil;

	@Override
	public SseEmitter subscribe(@PathVariable("auctionId") Integer auctionId,
			@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(value = "visitor", required = false, defaultValue = "false") boolean isVisitor,
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
			userKey = ("anonymous-" + System.nanoTime());
		}
		log.info("[SSE] Subscription to auction {} for userKey: {} (visitor: {}, token: {})",
				auctionId, userKey, isVisitor, token);
		return auctionSseService.subscribe(auctionId, userKey, isVisitor);
	}
}
