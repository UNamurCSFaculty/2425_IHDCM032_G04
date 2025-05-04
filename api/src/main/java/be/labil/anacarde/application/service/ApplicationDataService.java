package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.ApplicationDataDto;

public interface ApplicationDataService {

	/**
	 * Récupère les données de l'application.
	 *
	 * @return Les données de l'application.
	 */
	ApplicationDataDto getApplicationData();
}
