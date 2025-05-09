package be.labil.anacarde.domain.dto.db.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les administrateurs.")
public class AdminDetailDto extends UserDetailDto {
}
