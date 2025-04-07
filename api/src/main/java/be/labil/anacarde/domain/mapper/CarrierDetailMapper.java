package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.CarrierDetailDto;
import be.labil.anacarde.domain.model.Carrier;
import be.labil.anacarde.domain.model.Region;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CarrierDetailMapper extends GenericMapper<CarrierDetailDto, Carrier> {

	@Override
	@Mapping(source = "regions", target = "regionIds", qualifiedByName = "mapRegionsToIds")
	CarrierDetailDto toDto(Carrier carrier);

	@Override
	@Mapping(source = "regionIds", target = "regions", qualifiedByName = "mapIdsToRegions")
	Carrier toEntity(CarrierDetailDto dto);

	// Convertit les entités Region → liste d’IDs
	@Named("mapRegionsToIds")
	default Set<Integer> mapRegionsToIds(Set<Region> regions) {
		if (regions == null) return new HashSet<>();
		return regions.stream().map(Region::getId).collect(Collectors.toSet());
	}

	// Convertit la liste d’IDs → Set de Region (avec ID seulement)
	@Named("mapIdsToRegions")
	default Set<Region> mapIdsToRegions(Set<Integer> ids) {
		if (ids == null) return new HashSet<>();
		return ids.stream().map(id -> {
			Region r = new Region();
			r.setId(id);
			return r;
		}).collect(Collectors.toSet());
	}
}
