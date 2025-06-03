package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.StoreDetailDto;
import be.labil.anacarde.domain.dto.write.StoreUpdateDto;
import be.labil.anacarde.domain.model.*;
import jakarta.persistence.EntityManager;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
		MapperHelpers.class, AddressMapper.class, AddressUpdateMapper.class})
public abstract class StoreMapper {

	@Autowired
	protected EntityManager em;

	@Mapping(source = "user.id", target = "userId")
	public abstract StoreDetailDto toDto(Store store);

	@Mapping(target = "user", ignore = true)
	public abstract Store toEntity(StoreUpdateDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "user", ignore = true)
	public abstract Store partialUpdate(StoreUpdateDto dto, @MappingTarget Store entity);

	@AfterMapping
	protected void afterUpdateDto(StoreUpdateDto dto, @MappingTarget Store store) {
		if (dto.getUserId() != null) {
			store.setUser(em.getReference(User.class, dto.getUserId()));
		}
	}
}
