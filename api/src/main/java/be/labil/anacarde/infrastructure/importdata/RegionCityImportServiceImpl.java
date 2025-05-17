package be.labil.anacarde.infrastructure.importdata;

import static java.util.Map.entry;

import be.labil.anacarde.domain.model.City;
import be.labil.anacarde.domain.model.Region;
import be.labil.anacarde.infrastructure.persistence.CityRepository;
import be.labil.anacarde.infrastructure.persistence.RegionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
public class RegionCityImportServiceImpl implements RegionCityImportService {

	private final RegionRepository regionRepository;
	private final CityRepository cityRepository;
	private final ObjectMapper mapper;
	private final GeometryFactory geometryFactory = new GeometryFactory();

	private static final Map<String, Integer> REGION_IDS = Map.ofEntries(entry("Alibori", 1),
			entry("Atacora", 2), entry("Atlantique", 3), entry("Borgou", 4), entry("Collines", 5),
			entry("Couffo", 6), entry("Donga", 7), entry("Littoral", 8), entry("Mono", 9),
			entry("Ouémé", 10), entry("Plateau", 11), entry("Zou", 12));

	@Override
	public Map<Integer, Region> importRegions() {
		List<Region> regs = REGION_IDS.entrySet().stream()
				.map(e -> Region.builder().id((Integer) e.getValue()).name(e.getKey()).build())
				.collect(Collectors.toList());
		return regionRepository.saveAll(regs).stream()
				.collect(Collectors.toMap(Region::getId, Function.identity()));
	}

	@Override
	public List<City> importCities(String jsonClasspath, Map<Integer, Region> regions)
			throws IOException {
		List<CityImportDto> dtos = mapper.readValue(getClass().getResourceAsStream(jsonClasspath),
				new TypeReference<>() {
				});

		List<City> cities = dtos.stream().map(dto -> {
			double lat = Double.parseDouble(dto.getLat().replace(",", "."));
			double lng = Double.parseDouble(dto.getLon().replace(",", "."));
			Point location = geometryFactory.createPoint(new Coordinate(lng, lat));

			return City.builder().id(dto.getId()).name(dto.getName())
					.region(regions.get(dto.getRegion())).location(location).build();
		}).collect(Collectors.toList());

		return cityRepository.saveAll(cities);
	}
}
