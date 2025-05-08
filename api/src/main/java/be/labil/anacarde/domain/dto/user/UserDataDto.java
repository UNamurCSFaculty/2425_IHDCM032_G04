package be.labil.anacarde.domain.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Schema(description = "DTO minimal contenant uniquement l'ID et la date d'enregistrement")
@Builder
public class UserDataDto {

	@Schema(description = "Identifiant de l'utilisateur", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	@Schema(description = "Date d'enregistrement de l'utilisateur", example = "2025-04-01T09:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime registrationDate;

	@Schema(description = "Type d'utilisateur (admin, producer, transformer, quality_inspector, exporter, carrier)", accessMode = Schema.AccessMode.READ_ONLY)
	private String type;

	@Schema(description = "Id de la cooperation pour un producteur membre, 0 sinon)", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer cooperative;
}
