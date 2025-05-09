package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.StoreDetailDto;
import be.labil.anacarde.domain.model.Store;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = MapperHelpers.class)
public abstract class StoreMapper {

	@Mapping(source = "name", target = "name")
	@Mapping(source = "location", target = "location", qualifiedByName = "pointToString")
	@Mapping(source = "user.id", target = "userId")
	public abstract StoreDetailDto toDto(Store store);

	@Mapping(source = "name", target = "name")
	@Mapping(source = "location", target = "location", qualifiedByName = "stringToPoint")
	@Mapping(source = "userId", target = "user", qualifiedByName = "userIdToUser")
	public abstract Store toEntity(StoreDetailDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "name", target = "name")
	@Mapping(source = "location", target = "location", qualifiedByName = "stringToPoint")
	@Mapping(source = "userId", target = "user", qualifiedByName = "userIdToUser")
	public abstract Store partialUpdate(StoreDetailDto dto, @MappingTarget Store entity);
}
