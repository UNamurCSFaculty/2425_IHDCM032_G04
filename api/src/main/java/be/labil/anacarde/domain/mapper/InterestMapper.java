package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.InterestDto;
import be.labil.anacarde.domain.model.Interest;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {MapperHelpers.class, IntentionMapper.class,
		TraderDetailMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class InterestMapper {

	public abstract InterestDto toDto(Interest entity);

	public abstract Interest toEntity(InterestDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract Interest partialUpdate(InterestDto dto, @MappingTarget Interest entity);
}
