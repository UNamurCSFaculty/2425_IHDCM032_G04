package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.IntentionDto;
import be.labil.anacarde.domain.model.Intention;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {QualityMapper.class,
		TraderDetailMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IntentionMapper extends GenericMapper<IntentionDto, Intention> {

	@Override
	@Mapping(source = "quality", target = "quality")
	@Mapping(source = "buyer", target = "buyer")
	IntentionDto toDto(Intention entity);

	@Override
	@Mapping(source = "quality", target = "quality")
	@Mapping(source = "buyer", target = "buyer")
	Intention toEntity(IntentionDto dto);

	@Override
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "quality", target = "quality")
	@Mapping(source = "buyer", target = "buyer")
	Intention partialUpdate(IntentionDto dto, @MappingTarget Intention entity);
}
