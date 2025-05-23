package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.StoreDetailDto;
import be.labil.anacarde.domain.model.Store;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
		MapperHelpers.class, AddressMapper.class})
public abstract class StoreMapper {

	@Mapping(source = "name", target = "name")
	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "address", target = "address")
	public abstract StoreDetailDto toDto(Store store);

	@Mapping(source = "name", target = "name")
	@Mapping(source = "userId", target = "user", qualifiedByName = "userIdToUser")
	@Mapping(source = "address", target = "address")
	public abstract Store toEntity(StoreDetailDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "name", target = "name")
	@Mapping(source = "userId", target = "user", qualifiedByName = "userIdToUser")
	@Mapping(source = "address", target = "address")
	public abstract Store partialUpdate(StoreDetailDto dto, @MappingTarget Store entity);
}
