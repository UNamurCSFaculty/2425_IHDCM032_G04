package be.labil.anacarde.infrastructure.importdata;

import be.labil.anacarde.domain.model.City;
import be.labil.anacarde.domain.model.Region;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface RegionCityImportService {
	/**
	 * Importe les régions depuis la constante REGION_IDS
	 */
	Map<Integer, Region> importRegions();

	/**
	 * Importe les villes depuis le JSON et associe correctement régions + géométrie.
	 */
	List<City> importCities(String jsonClasspath, Map<Integer, Region> regions) throws IOException;
}
