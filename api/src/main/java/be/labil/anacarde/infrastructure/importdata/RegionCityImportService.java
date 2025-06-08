package be.labil.anacarde.infrastructure.importdata;

import be.labil.anacarde.domain.model.City;
import be.labil.anacarde.domain.model.Region;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Service responsable de l'importation des données de régions et de villes depuis des sources
 * externes (par exemple des fichiers JSON).
 * <p>
 * Fournit des méthodes pour :
 * <ul>
 * <li>Importer la liste des régions identifiées par un identifiant constant.</li>
 * <li>Importer les villes à partir d’un fichier JSON et les associer aux régions et géométries
 * correspondantes.</li>
 * </ul>
 */
public interface RegionCityImportService {

	/**
	 * Importe les régions depuis la source définie (constante {@code REGION_IDS}).
	 * <p>
	 * Cette méthode doit construire et retourner une map dont la clé est l’identifiant de la région
	 * et la valeur est l’objet {@link Region} correspondant.
	 *
	 * @return une {@link Map} d’identifiants de région vers {@link Region}
	 */
	Map<Integer, Region> importRegions();

	/**
	 * Importe les villes à partir d’un fichier JSON situé sur le classpath, crée les instances
	 * {@link City} et les associe aux régions fournies.
	 * <p>
	 * Chaque ville doit également être enrichie de sa géométrie (coordonnées).
	 *
	 * @param jsonClasspath
	 *            chemin du fichier JSON dans le classpath
	 * @param regions
	 *            map des régions déjà importées (clé : ID de région, valeur : {@link Region})
	 * @return une liste d’objets {@link City} importés et configurés
	 * @throws IOException
	 *             en cas d’erreur de lecture du fichier JSON
	 */
	List<City> importCities(String jsonClasspath, Map<Integer, Region> regions) throws IOException;
}
