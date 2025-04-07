package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.ProducerDetailDto;
import be.labil.anacarde.domain.model.Producer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CooperativeMapper.class})
public interface ProducerDetailMapper extends GenericMapper<ProducerDetailDto, Producer> {

	@Override
	@Mapping(source = "cooperative", target = "cooperative")
	Producer toEntity(ProducerDetailDto dto);

	@Override
	@Mapping(source = "cooperative", target = "cooperative")
	ProducerDetailDto toDto(Producer entity);
}
