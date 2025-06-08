package be.labil.anacarde.presentation.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * API pour gérer les notifications en temps réel via Server-Sent Events (SSE). Permet aux
 * utilisateurs de s'abonner pour recevoir des notifications en continu.
 */
@Validated
@RequestMapping("/api/notifications")
@SecurityRequirement(name = "jwt")
@Tag(name = "sse", description = "Gestion des notifications (Server-Sent Events)")
public interface NotificationSseApi {
	@GetMapping("/stream")
	SseEmitter subscribe(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(value = "token", required = false) String token);
}
