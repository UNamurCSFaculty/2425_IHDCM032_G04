package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.StoreDetailDto;
import be.labil.anacarde.domain.model.Store;
import be.labil.anacarde.domain.model.User;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StoreMapper extends GenericMapper<StoreDetailDto, Store> {

	@Override
	@Mapping(source = "location", target = "location", qualifiedByName = "pointToString")
	@Mapping(source = "user.id", target = "userId")
	StoreDetailDto toDto(Store store);

	@Override
	@Mapping(source = "location", target = "location", qualifiedByName = "stringToPoint")
	@Mapping(source = "userId", target = "user", qualifiedByName = "userIdToUser")
	Store toEntity(StoreDetailDto dto);

	@Named("pointToString")
	default String pointToString(Point point) {
		return point != null ? new WKTWriter().write(point) : null;
	}

	@Named("stringToPoint")
	default Point stringToPoint(String wkt) {
		if (wkt == null || wkt.isBlank()) return null;
		try {
			return (Point) new WKTReader(new GeometryFactory()).read(wkt);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Format WKT invalide : " + wkt, e);
		}
	}

	@Named("userIdToUser")
	default User userIdToUser(Integer userId) {
		if (userId == null) return null;
		User user = new User() {
		}; // si User est abstract, on crée une instance anonyme
		user.setId(userId);
		return user;
	}

	@Override
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "location", target = "location", qualifiedByName = "stringToPoint")
	@Mapping(source = "userId", target = "user", qualifiedByName = "userIdToUser")
	Store partialUpdate(StoreDetailDto dto, @MappingTarget Store entity);
}
