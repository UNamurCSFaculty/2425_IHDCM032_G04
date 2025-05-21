package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.model.City;
import be.labil.anacarde.domain.model.Region;

public interface GeoService {

	City findCityById(Integer cityId);
	Region findRegionByCityId(City city);
}
