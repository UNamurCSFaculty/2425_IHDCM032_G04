package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.InterestDto;
import be.labil.anacarde.domain.model.Interest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {IntentionMapper.class,
		TraderDetailMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InterestMapper extends GenericMapper<InterestDto, Interest> {

	@Override
	@Mapping(source = "intention", target = "intention")
	@Mapping(source = "buyer", target = "buyer")
	InterestDto toDto(Interest entity);

	@Override
	@Mapping(source = "intention", target = "intention")
	@Mapping(source = "buyer", target = "buyer")
	Interest toEntity(InterestDto dto);

	@Override
	@Mapping(source = "intention", target = "intention")
	@Mapping(source = "buyer", target = "buyer")
	Interest partialUpdate(InterestDto dto, @MappingTarget Interest entity);
}
