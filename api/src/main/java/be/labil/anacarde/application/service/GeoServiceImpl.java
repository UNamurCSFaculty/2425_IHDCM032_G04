package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.model.City;
import be.labil.anacarde.domain.model.Region;
import be.labil.anacarde.infrastructure.persistence.CityRepository;
import be.labil.anacarde.infrastructure.persistence.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GeoServiceImpl implements GeoService {

	private final RegionRepository regionRepository;
	private final CityRepository cityRepository;

	@Override
	public City findCityById(Integer cityId) {
		return cityRepository.findById(cityId).orElseThrow(
				() -> new ResourceNotFoundException("City not found with id: " + cityId));
	}

	@Override
	public Region findRegionByCityId(City city) {
		Integer id = city.getRegion().getId();
		return regionRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("Region not found with  id: " + id));
	}
}
