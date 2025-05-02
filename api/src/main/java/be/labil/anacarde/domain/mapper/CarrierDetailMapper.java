package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.user.CarrierDetailDto;
import be.labil.anacarde.domain.model.Carrier;
import be.labil.anacarde.domain.model.Region;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {HibernateLazyCondition.class, RoleMapper.class,
		LanguageMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CarrierDetailMapper {

	@Mapping(source = "regions", target = "regionIds", qualifiedByName = "mapRegionsToIds")
	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	public abstract CarrierDetailDto toDto(Carrier carrier);

	@Mapping(source = "regionIds", target = "regions", qualifiedByName = "mapIdsToRegions")
	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	public abstract Carrier toEntity(CarrierDetailDto dto);

	// Convertit les entités Region → liste d’IDs
	@Named("mapRegionsToIds")
	public static Set<Integer> mapRegionsToIds(Set<Region> regions) {
		if (regions == null) return new HashSet<>();
		return regions.stream().map(Region::getId).collect(Collectors.toSet());
	}

	// Convertit la liste d’IDs → Set de Region (avec ID seulement)
	@Named("mapIdsToRegions")
	public static Set<Region> mapIdsToRegions(Set<Integer> ids) {
		if (ids == null) return new HashSet<>();
		return ids.stream().map(id -> {
			Region r = new Region();
			r.setId(id);
			return r;
		}).collect(Collectors.toSet());
	}
}
