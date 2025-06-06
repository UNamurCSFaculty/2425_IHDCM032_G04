package be.labil.anacarde.presentation.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * API SSE pour la consultation live d'une enchère (notifications pour tous les visiteurs de la
 * page).
 */
@Validated
@RequestMapping("/api/auctions/{auctionId}/sse")
@SecurityRequirement(name = "jwt")
@Tag(name = "sse")
public interface AuctionSseApi {
	@GetMapping
	SseEmitter subscribe(@PathVariable("auctionId") Integer auctionId,
			@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(value = "visitor", required = false, defaultValue = "false") boolean isVisitor,
			@RequestParam(value = "token", required = false) String token);
}
