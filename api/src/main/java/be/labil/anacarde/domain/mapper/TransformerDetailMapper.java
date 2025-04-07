package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.TransformerDetailDto;
import be.labil.anacarde.domain.model.Transformer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransformerDetailMapper extends GenericMapper<TransformerDetailDto, Transformer> {

	@Override
	Transformer toEntity(TransformerDetailDto dto);

	@Override
	TransformerDetailDto toDto(Transformer entity);
}
