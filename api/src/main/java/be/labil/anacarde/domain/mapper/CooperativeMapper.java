package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.CooperativeDto;
import be.labil.anacarde.domain.model.Cooperative;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {HibernateLazyCondition.class, ProducerDetailMapper.class})
public abstract class CooperativeMapper {

	@Mapping(source = "presidentId", target = "president.id")
	public abstract Cooperative toEntity(CooperativeDto dto);

	// on ignore complètement le mapping automatique de presidentId
	@Mapping(source = "president.id", target = "presidentId")
	public abstract CooperativeDto toDto(Cooperative entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "president", ignore = true)
	public abstract Cooperative partialUpdate(CooperativeDto dto, @MappingTarget Cooperative entity);
}
