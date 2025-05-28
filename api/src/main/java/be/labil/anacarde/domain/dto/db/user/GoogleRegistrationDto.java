package be.labil.anacarde.domain.dto.db.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO utilisé UNIQUEMENT pour le endpoint /api/users/google -> il contient seulement l'ID-token
 * Google.
 */
@Data
public class GoogleRegistrationDto {

	@NotBlank
	@Schema(description = "ID-token Google provenant du front", example = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...")
	private String idToken;
}
