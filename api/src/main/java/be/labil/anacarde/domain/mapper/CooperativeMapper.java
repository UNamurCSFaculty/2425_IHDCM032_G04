package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.CooperativeDto;
import be.labil.anacarde.domain.model.Cooperative;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProducerDetailMapper.class})
public interface CooperativeMapper extends GenericMapper<CooperativeDto, Cooperative> {

	@Override
	@Mapping(source = "presidentId", target = "president.id")
	Cooperative toEntity(CooperativeDto dto);

	@Override
	@Mapping(source = "president.id", target = "presidentId")
	CooperativeDto toDto(Cooperative entity);
}
