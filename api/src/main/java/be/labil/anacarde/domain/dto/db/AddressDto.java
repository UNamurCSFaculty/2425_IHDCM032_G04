package be.labil.anacarde.domain.dto.db;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Schema(description = "DTO pour l'entité Adresse.")
@SuperBuilder
@NoArgsConstructor
public class AddressDto {
	private String street;
	private Integer cityId;
	private Integer regionId;
}
