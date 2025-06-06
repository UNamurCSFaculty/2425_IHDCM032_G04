package be.labil.anacarde.application.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dto d'un message d'erreur unique
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Détail d'une erreur individuelle")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetail {

	@Schema(description = "Nom du champ en erreur (absent pour erreurs globales)", example = "email")
	private String field;

	@Schema(description = "Code d'erreur détaillé", example = "user.email.invalid_format", required = true)
	private String code;

	@Schema(description = "Message décrivant l'erreur", example = "Le format de l'email est invalide", required = true)
	private String message;
}
