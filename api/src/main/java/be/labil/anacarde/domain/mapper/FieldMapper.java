package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.FieldDto;
import be.labil.anacarde.domain.model.Field;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {MapperHelpers.class,
		UserDetailMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class FieldMapper {

	@Mapping(source = "location", target = "location", qualifiedByName = "pointToString")
	@Mapping(source = "producer", target = "producer")
	public abstract FieldDto toDto(Field entity);

	@Mapping(source = "location", target = "location", qualifiedByName = "stringToPoint")
	@Mapping(source = "producer", target = "producer")
	public abstract Field toEntity(FieldDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "location", target = "location", qualifiedByName = "stringToPoint")
	@Mapping(source = "producer", target = "producer")
	public abstract Field partialUpdate(FieldDto dto, @MappingTarget Field entity);
}
