package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.AddressDto;
import be.labil.anacarde.domain.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {MapperHelpers.class})
public abstract class AddressMapper {

	@Mapping(source = "location", target = "location", qualifiedByName = "stringToPoint")
	@Mapping(source = "cityId", target = "city.id")
	@Mapping(source = "regionId", target = "region.id")
	public abstract Address toEntity(AddressDto dto);

	@Mapping(source = "location", target = "location", qualifiedByName = "pointToString")
	@Mapping(source = "city.id", target = "cityId")
	@Mapping(source = "region.id", target = "regionId")
	public abstract AddressDto toDto(Address entity);
}
