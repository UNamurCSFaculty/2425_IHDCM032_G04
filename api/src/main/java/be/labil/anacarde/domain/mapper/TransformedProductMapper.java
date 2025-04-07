package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.TransformedProductDto;
import be.labil.anacarde.domain.model.TransformedProduct;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {
		TransformerDetailMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransformedProductMapper extends GenericMapper<TransformedProductDto, TransformedProduct> {

	@Override
	@Mapping(source = "transformer", target = "transformer")
	TransformedProductDto toDto(TransformedProduct entity);

	@Override
	@Mapping(source = "transformer", target = "transformer")
	TransformedProduct toEntity(TransformedProductDto dto);

	@Override
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "transformer", target = "transformer")
	TransformedProduct partialUpdate(TransformedProductDto dto, @MappingTarget TransformedProduct entity);
}
