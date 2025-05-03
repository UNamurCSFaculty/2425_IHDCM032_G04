package be.labil.anacarde.application.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Détail d'une erreur individuelle")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetail {

	@Schema(description = "Nom du champ en erreur (absent pour erreurs globales)", example = "email", required = false)
	private String field;

	@Schema(description = "Message décrivant l'erreur", example = "Le format de l'email est invalide", required = true)
	private String message;
}
