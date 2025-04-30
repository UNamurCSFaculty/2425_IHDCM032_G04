package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.HarvestProductDto;
import be.labil.anacarde.domain.dto.ProductDto;
import be.labil.anacarde.domain.dto.TransformedProductDto;
import be.labil.anacarde.domain.model.HarvestProduct;
import be.labil.anacarde.domain.model.Product;
import be.labil.anacarde.domain.model.TransformedProduct;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {DocumentMapper.class, UserDetailMapper.class, StoreMapper.class,
		FieldDetailMapper.class})
public interface ProductMapper extends GenericMapper<ProductDto, Product> {

	default Product toEntity(ProductDto dto) {
		if (dto instanceof HarvestProductDto) {
			return toEntity((HarvestProductDto) dto);
		} else if (dto instanceof TransformedProductDto) {
			return toEntity((TransformedProductDto) dto);
		}
		throw new IllegalArgumentException("Type de ProductDto non supporté : " + dto.getClass().getName());
	}

	default ProductDto toDto(Product entity) {
		if (entity instanceof HarvestProduct) {
			return toDto((HarvestProduct) entity);
		} else if (entity instanceof TransformedProduct) {
			return toDto((TransformedProduct) entity);
		}
		throw new IllegalArgumentException("Type de Product non supporté : " + entity.getClass().getName());
	}

	default Product partialUpdate(ProductDto dto, Product entity) {
		if (dto instanceof HarvestProductDto && entity instanceof HarvestProduct) {
			return partialUpdate((HarvestProductDto) dto, (HarvestProduct) entity);
		} else if (dto instanceof TransformedProductDto && entity instanceof TransformedProduct) {
			return partialUpdate((TransformedProductDto) dto, (TransformedProduct) entity);
		}
		throw new IllegalArgumentException("Type de Product non supporté : " + entity.getClass().getName());
	}

	// Mapping vers l'entité
	@Mapping(source = "store", target = "store")
	@Mapping(source = "producer", target = "producer")
	@Mapping(source = "field", target = "field")
	@Mapping(source = "qualityControl", target = "qualityControl")
	HarvestProduct toEntity(HarvestProductDto dto);

	// Mapping inverse vers le DTO
	@Mapping(source = "store", target = "store")
	@Mapping(source = "producer", target = "producer")
	@Mapping(source = "field", target = "field")
	@Mapping(source = "qualityControl", target = "qualityControl")
	HarvestProductDto toDto(HarvestProduct entity);

	// Mapping vers l'entité
	@Mapping(source = "identifier", target = "identifier")
	@Mapping(source = "location", target = "location")
	@Mapping(source = "transformer", target = "transformer")
	@Mapping(source = "qualityControl", target = "qualityControl")
	TransformedProduct toEntity(TransformedProductDto dto);

	// Mapping inverse vers le DTO
	@Mapping(source = "identifier", target = "identifier")
	@Mapping(source = "location", target = "location")
	@Mapping(source = "transformer", target = "transformer")
	@Mapping(source = "qualityControl", target = "qualityControl")
	TransformedProductDto toDto(TransformedProduct entity);

	@Mapping(source = "identifier", target = "identifier")
	@Mapping(source = "location", target = "location")
	@Mapping(source = "transformer", target = "transformer")
	@Mapping(source = "qualityControl", target = "qualityControl")
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	TransformedProduct partialUpdate(TransformedProductDto dto, @MappingTarget TransformedProduct entity);

	@Mapping(source = "store", target = "store")
	@Mapping(source = "producer", target = "producer")
	@Mapping(source = "field", target = "field")
	@Mapping(source = "qualityControl", target = "qualityControl")
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	HarvestProduct partialUpdate(HarvestProductDto dto, @MappingTarget HarvestProduct entity);
}
