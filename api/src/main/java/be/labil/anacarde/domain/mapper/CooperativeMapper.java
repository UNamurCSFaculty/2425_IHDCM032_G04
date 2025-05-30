package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.CooperativeDto;
import be.labil.anacarde.domain.dto.write.CooperativeUpdateDto;
import be.labil.anacarde.domain.model.Cooperative;
import be.labil.anacarde.domain.model.Producer;
import jakarta.persistence.EntityManager;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {MapperHelpers.class, ProducerDetailMapper.class})
public abstract class CooperativeMapper {

	@Autowired
	protected EntityManager em;

	@Mapping(source = "presidentId", target = "president.id")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "members", ignore = true)
	public abstract Cooperative toEntity(CooperativeUpdateDto dto);

	// on ignore complètement le mapping automatique de presidentId
	@Mapping(source = "president.id", target = "presidentId")
	public abstract CooperativeDto toDto(Cooperative entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "president", ignore = true)
	@Mapping(target = "members", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract Cooperative partialUpdate(CooperativeUpdateDto dto,
			@MappingTarget Cooperative entity);

	@AfterMapping
	protected void afterUpdateDto(CooperativeUpdateDto dto,
			@MappingTarget Cooperative cooperative) {
		if (dto.getPresidentId() != null) {
			cooperative.setPresident(em.getReference(Producer.class, dto.getPresidentId()));
		}
	}
}
