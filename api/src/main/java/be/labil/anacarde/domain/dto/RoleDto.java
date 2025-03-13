package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objet de transfert de données pour les rôles")
public class RoleDto {
    /** Identifiant unique du rôle. */
    @Schema(
            description = "Identifiant du rôle",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    /** Nom du rôle. */
    @Schema(
            description = "Nom du rôle",
            example = "ROLE_USER",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Le nom du rôle est requis")
    private String name;
}
