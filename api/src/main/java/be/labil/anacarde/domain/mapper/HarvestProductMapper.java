package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.HarvestProductDto;
import be.labil.anacarde.domain.model.HarvestProduct;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {StoreMapper.class, ProducerDetailMapper.class, FieldDetailMapper.class,
		TransformedProductMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HarvestProductMapper extends GenericMapper<HarvestProductDto, HarvestProduct> {

	@Override
	@Mapping(source = "store", target = "store")
	@Mapping(source = "producer", target = "producer")
	@Mapping(source = "field", target = "field")
	@Mapping(source = "transformedProduct", target = "transformedProduct")
	HarvestProductDto toDto(HarvestProduct entity);

	@Override
	@Mapping(source = "store", target = "store")
	@Mapping(source = "producer", target = "producer")
	@Mapping(source = "field", target = "field")
	@Mapping(source = "transformedProduct", target = "transformedProduct")
	HarvestProduct toEntity(HarvestProductDto dto);

	@Override
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "store", target = "store")
	@Mapping(source = "producer", target = "producer")
	@Mapping(source = "field", target = "field")
	@Mapping(source = "transformedProduct", target = "transformedProduct")
	HarvestProduct partialUpdate(HarvestProductDto dto, @MappingTarget HarvestProduct entity);
}
