package be.labil.anacarde.domain.dto.db.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Réponse d’authentification Google")
public class GoogleAuthResponse {
	@Schema(description = "Jeton JWT à utiliser dans Authorization: Bearer …", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9…")
	private String token;
}
