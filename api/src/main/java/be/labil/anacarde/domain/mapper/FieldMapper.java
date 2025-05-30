package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.FieldDto;
import be.labil.anacarde.domain.model.Field;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {MapperHelpers.class, UserDetailMapper.class,
		AddressMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class FieldMapper {

	public abstract FieldDto toDto(Field entity);

	public abstract Field toEntity(FieldDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract Field partialUpdate(FieldDto dto, @MappingTarget Field entity);
}
