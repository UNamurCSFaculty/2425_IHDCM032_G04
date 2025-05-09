package be.labil.anacarde.domain.dto.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public abstract class BaseDto implements Serializable {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Schema(description = "Identifiant unique", example = "1", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer id;
}
