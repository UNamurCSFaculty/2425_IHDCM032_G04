package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.db.ApplicationDataDto;

/**
 * Services de données de l'application. Ces services fournissent des informations générales sur
 * l'application, telles que la version, les statistiques d'utilisation, etc.
 */
public interface ApplicationDataService {

	/**
	 * Récupère les données de l'application.
	 *
	 * @return Les données de l'application.
	 */
	ApplicationDataDto getApplicationData();
}
