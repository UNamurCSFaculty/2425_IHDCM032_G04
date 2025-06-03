package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.write.AddressUpdateDto;
import be.labil.anacarde.domain.model.Address;
import be.labil.anacarde.domain.model.City;
import be.labil.anacarde.domain.model.Region;
import jakarta.persistence.EntityManager;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {MapperHelpers.class})
public abstract class AddressUpdateMapper {

	@Autowired
	protected EntityManager em;

	@Mapping(source = "location", target = "location", qualifiedByName = "stringToPoint")
	@Mapping(target = "city", ignore = true)
	@Mapping(target = "region", ignore = true)
	public abstract Address toEntity(AddressUpdateDto dto);

	@Mapping(source = "location", target = "location", qualifiedByName = "pointToString")
	@Mapping(source = "city.id", target = "cityId")
	@Mapping(source = "region.id", target = "regionId")
	public abstract AddressUpdateDto toDto(Address entity);

	@AfterMapping
	protected void afterUpdateDto(AddressUpdateDto dto, @MappingTarget Address entity) {
		if (dto.getCityId() != null) {
			entity.setCity(em.getReference(City.class, dto.getCityId()));
		}
		if (dto.getRegionId() != null) {
			entity.setRegion(em.getReference(Region.class, dto.getRegionId()));
		}
	}
}
