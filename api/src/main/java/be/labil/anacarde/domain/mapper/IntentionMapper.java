package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.IntentionDto;
import be.labil.anacarde.domain.model.Intention;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {MapperHelpers.class, QualityMapper.class,
		TraderDetailMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class IntentionMapper {

	@Mapping(source = "quality", target = "quality")
	@Mapping(source = "buyer", target = "buyer")
	public abstract IntentionDto toDto(Intention entity);

	@Mapping(source = "quality", target = "quality")
	@Mapping(source = "buyer", target = "buyer")
	public abstract Intention toEntity(IntentionDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "quality", target = "quality")
	@Mapping(source = "buyer", target = "buyer")
	public abstract Intention partialUpdate(IntentionDto dto, @MappingTarget Intention entity);
}
