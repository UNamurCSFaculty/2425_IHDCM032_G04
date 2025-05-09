package be.labil.anacarde.domain.dto.write;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Requête pour envoyer un message de contact")
public class ContactRequestDto {

	@Schema(description = "Nom du contact", example = "Jean Dupont", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le nom est obligatoire")
	private String name;

	@Schema(description = "Adresse email du contact", example = "jean.dupont@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "L'email est obligatoire")
	@Email(message = "Format d’email invalide")
	private String email;

	@Schema(description = "Message du contact", example = "Bonjour, j'ai une question...", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le message est obligatoire")
	@Size(max = 500, message = "Le message ne doit pas dépasser {max} caractères")
	private String message;
}
