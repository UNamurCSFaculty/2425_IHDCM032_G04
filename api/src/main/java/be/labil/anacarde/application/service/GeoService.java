package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.model.City;
import be.labil.anacarde.domain.model.Region;

/**
 * Service pour la gestion géographique. Permet de récupérer des informations sur les villes et les
 * régions.
 */
public interface GeoService {

	/**
	 * Récupère une ville par son identifiant.
	 *
	 * @param cityId
	 *            L'identifiant de la ville à récupérer.
	 * @return La ville correspondante, ou null si aucune ville n'est trouvée.
	 */
	City findCityById(Integer cityId);

	/**
	 * Récupère une région par l'identifiant d'une ville.
	 *
	 * @param city
	 *            La ville dont on veut connaître la région.
	 * @return La région correspondante à la ville, ou null si aucune région n'est trouvée.
	 */
	Region findRegionByCityId(City city);
}
