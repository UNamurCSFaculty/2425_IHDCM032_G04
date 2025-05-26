package be.labil.anacarde.domain.dto.db.user;

import be.labil.anacarde.domain.dto.write.user.UserUpdateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GoogleRegistrationDto extends UserUpdateDto {
	@NotBlank
	@Schema(description = "ID-Token Google issu du front", example = "eyJhbGciOiJSUzI1NiIsInR5…")
	private String idToken;
}
