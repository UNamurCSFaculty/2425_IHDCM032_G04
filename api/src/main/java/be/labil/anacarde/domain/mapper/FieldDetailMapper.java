package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.FieldDetailDto;
import be.labil.anacarde.domain.model.Field;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {ProducerDetailMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FieldDetailMapper extends GenericMapper<FieldDetailDto, Field> {

	@Override
	@Mapping(source = "location", target = "location", qualifiedByName = "pointToString")
	@Mapping(source = "producer", target = "producer")
	FieldDetailDto toDto(Field entity);

	@Override
	@Mapping(source = "location", target = "location", qualifiedByName = "stringToPoint")
	@Mapping(source = "producer", target = "producer")
	Field toEntity(FieldDetailDto dto);

	@Override
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "location", target = "location", qualifiedByName = "stringToPoint")
	@Mapping(source = "producer", target = "producer")
	Field partialUpdate(FieldDetailDto dto, @MappingTarget Field entity);

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
