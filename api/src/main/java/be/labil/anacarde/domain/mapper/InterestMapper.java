package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.InterestDto;
import be.labil.anacarde.domain.model.Interest;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {HibernateLazyCondition.class, IntentionMapper.class,
		TraderDetailMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class InterestMapper {

	@Mapping(source = "intention", target = "intention")
	@Mapping(source = "buyer", target = "buyer")
	public abstract InterestDto toDto(Interest entity);

	@Mapping(source = "intention", target = "intention")
	@Mapping(source = "buyer", target = "buyer")
	public abstract Interest toEntity(InterestDto dto);

	@Mapping(source = "intention", target = "intention")
	@Mapping(source = "buyer", target = "buyer")
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract Interest partialUpdate(InterestDto dto, @MappingTarget Interest entity);
}
