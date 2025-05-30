package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.StoreDetailDto;
import be.labil.anacarde.domain.model.Store;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
		MapperHelpers.class, AddressMapper.class})
public abstract class StoreMapper {

	@Mapping(source = "user.id", target = "userId")
	public abstract StoreDetailDto toDto(Store store);

	@Mapping(source = "userId", target = "user", qualifiedByName = "userIdToUser")
	public abstract Store toEntity(StoreDetailDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "userId", target = "user", qualifiedByName = "userIdToUser")
	public abstract Store partialUpdate(StoreDetailDto dto, @MappingTarget Store entity);
}
