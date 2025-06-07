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
 * Interface REST pour la diffusion d’événements Server-Sent Events (SSE) liés à une enchère
 * spécifique.
 * <p>
 * Permet aux clients (utilisateurs authentifiés ou visiteurs) de s’abonner et de recevoir en temps
 * réel les mises à jour d’une enchère donnée.
 */
@Validated
@RequestMapping("/api/auctions/{auctionId}/sse")
@SecurityRequirement(name = "jwt")
@Tag(name = "sse")
public interface AuctionSseApi {
	/**
	 * Ouvre une connexion SSE pour l’enchère spécifiée.
	 * <p>
	 * Le client reçoit un {@link SseEmitter} via lequel seront envoyés les événements (nouvelles
	 * offres, statuts, etc.).
	 *
	 * @param auctionId
	 *            identifiant de l’enchère à suivre en temps réel
	 * @param userDetails
	 *            informations de l’utilisateur authentifié (peut être null si visitor=false)
	 * @param isVisitor
	 *            indique si l’abonné est un simple visiteur (true) ou un participant (false)
	 * @param token
	 *            jeton d’accès pour les visiteurs non authentifiés (facultatif)
	 * @return un {@link SseEmitter} pour diffuser les événements de l’enchère
	 */
	@GetMapping
	SseEmitter subscribe(@PathVariable("auctionId") Integer auctionId,
			@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(value = "visitor", required = false, defaultValue = "false") boolean isVisitor,
			@RequestParam(value = "token", required = false) String token);
}
