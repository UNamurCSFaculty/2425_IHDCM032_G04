package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.write.user.update.AddressUpdateDto;
import be.labil.anacarde.domain.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {MapperHelpers.class})
public abstract class AddressUpdateMapper {

	@Mapping(source = "location", target = "location", qualifiedByName = "stringToPoint")
	@Mapping(source = "cityId", target = "city.id")
	@Mapping(source = "regionId", target = "region.id")
	public abstract Address toEntity(AddressUpdateDto dto);

	@Mapping(source = "location", target = "location", qualifiedByName = "pointToString")
	@Mapping(source = "city.id", target = "cityId")
	@Mapping(source = "region.id", target = "regionId")
	public abstract AddressUpdateDto toDto(Address entity);

}
