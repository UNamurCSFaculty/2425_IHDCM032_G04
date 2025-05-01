package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.FieldDto;
import be.labil.anacarde.domain.model.Field;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserDetailMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FieldMapper extends GenericMapper<FieldDto, Field> {

	@Override
	@Mapping(source = "location", target = "location", qualifiedByName = "pointToString")
	@Mapping(source = "producer", target = "producer")
	FieldDto toDto(Field entity);

	@Override
	@Mapping(source = "location", target = "location", qualifiedByName = "stringToPoint")
	@Mapping(source = "producer", target = "producer")
	Field toEntity(FieldDto dto);

	@Override
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "location", target = "location", qualifiedByName = "stringToPoint")
	@Mapping(source = "producer", target = "producer")
	Field partialUpdate(FieldDto dto, @MappingTarget Field entity);

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
}
