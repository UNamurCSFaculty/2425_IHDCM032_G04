package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for Role")
public class RoleDto {
    /** Unique identifier of the role. */
    @Schema(
            description = "Role identifier",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    /** Name of the role. */
    @Schema(
            description = "Role name",
            example = "ROLE_USER",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
}
