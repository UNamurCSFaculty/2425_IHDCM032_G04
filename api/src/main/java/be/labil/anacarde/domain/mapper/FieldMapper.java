package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.FieldDto;
import be.labil.anacarde.domain.dto.write.FieldUpdateDto;
import be.labil.anacarde.domain.model.Field;
import be.labil.anacarde.domain.model.Producer;
import jakarta.persistence.EntityManager;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {MapperHelpers.class, UserDetailMapper.class,
		AddressMapper.class,
		AddressUpdateMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class FieldMapper {

	@Autowired
	protected EntityManager em;

	public abstract FieldDto toDto(Field entity);

	public abstract Field toEntity(FieldUpdateDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract Field partialUpdate(FieldUpdateDto dto, @MappingTarget Field entity);

	@AfterMapping
	protected void afterUpdateDto(FieldUpdateDto dto, @MappingTarget Field store) {
		if (dto.getProducerId() != null) {
			store.setProducer(em.getReference(Producer.class, dto.getProducerId()));
		}
	}
}
