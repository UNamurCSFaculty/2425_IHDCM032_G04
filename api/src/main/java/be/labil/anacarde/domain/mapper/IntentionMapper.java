package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.IntentionDto;
import be.labil.anacarde.domain.model.Intention;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {MapperHelpers.class, QualityMapper.class,
		TraderDetailMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class IntentionMapper {

	public abstract IntentionDto toDto(Intention entity);

	public abstract Intention toEntity(IntentionDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract Intention partialUpdate(IntentionDto dto, @MappingTarget Intention entity);
}
