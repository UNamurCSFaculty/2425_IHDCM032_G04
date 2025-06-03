package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.db.GlobalSettingsDto;
import be.labil.anacarde.domain.dto.write.GlobalSettingsUpdateDto;

/**
 * Ce service offre des méthodes permettant de récupérer et mettre à jour les paramètres globaux du
 * système.
 */
public interface GlobalSettingsService {

	/**
	 * Récupère les paramètres globaux du système.
	 *
	 * @return Un GlobalSettingsDto représentant les paramètres globaux actuels.
	 */
	GlobalSettingsDto getGlobalSettings();

	/**
	 * Met à jour les paramètres globaux du système avec les informations fournies dans le
	 * GlobalSettingsUpdateDto.
	 *
	 * @param dto
	 *            Le GlobalSettingsUpdateDto contenant les informations mises à jour.
	 * @return Un GlobalSettingsDto représentant les paramètres globaux mis à jour.
	 */
	GlobalSettingsDto updateGlobalSettings(GlobalSettingsUpdateDto dto);
}
