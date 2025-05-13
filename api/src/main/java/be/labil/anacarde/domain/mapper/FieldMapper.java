package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.FieldDto;
import be.labil.anacarde.domain.model.Field;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {MapperHelpers.class, UserDetailMapper.class,
		AddressMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class FieldMapper {

	@Mapping(source = "producer", target = "producer")
	@Mapping(source = "address", target = "address")
	public abstract FieldDto toDto(Field entity);

	@Mapping(source = "producer", target = "producer")
	@Mapping(source = "address", target = "address")
	public abstract Field toEntity(FieldDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "producer", target = "producer")
	@Mapping(source = "address", target = "address")
	public abstract Field partialUpdate(FieldDto dto, @MappingTarget Field entity);
}
