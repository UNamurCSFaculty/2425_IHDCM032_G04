package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.CooperativeDto;
import be.labil.anacarde.domain.model.Cooperative;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProducerDetailMapper.class})
public interface CooperativeMapper extends GenericMapper<CooperativeDto, Cooperative> {

	@Override
	@Mapping(source = "president", target = "president")
	Cooperative toEntity(CooperativeDto dto);

	@Override
	@Mapping(source = "president", target = "president")
	CooperativeDto toDto(Cooperative entity);
}
