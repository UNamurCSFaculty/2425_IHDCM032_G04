package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.AddressDto;
import be.labil.anacarde.domain.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class AddressMapper {

	@Mapping(source = "cityId", target = "city.id")
	@Mapping(source = "regionId", target = "region.id")
	public abstract Address toEntity(AddressDto dto);

	@Mapping(source = "city.id", target = "cityId")
	@Mapping(source = "region.id", target = "regionId")
	public abstract AddressDto toDto(Address entity);
}
