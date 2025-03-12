package be.labil.anacarde.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
/**
 * @brief Data Transfer Object for Role entities.
 */
public class RoleDto {
    /**
     * @brief Unique identifier of the role.
     */
    private Integer id;
    /**
     * @brief Name of the role.
     */
    private String name;
}