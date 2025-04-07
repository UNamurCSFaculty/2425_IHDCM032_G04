package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.HarvestProductDto;
import be.labil.anacarde.domain.dto.ProductDto;
import be.labil.anacarde.domain.dto.TransformedProductDto;
import be.labil.anacarde.domain.model.HarvestProduct;
import be.labil.anacarde.domain.model.Product;
import be.labil.anacarde.domain.model.TransformedProduct;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring")
public interface ProductMapper extends GenericMapper<ProductDto, Product> {

	@Override
	Product toEntity(ProductDto dto);

	@Override
	ProductDto toDto(Product entity);

	/**
	 * Méthode factory pour créer une instance concrète de Product lors du mapping depuis ProductDto vers Product.
	 */
	@ObjectFactory
	default Product createEntity(ProductDto dto) {
		if (dto instanceof HarvestProductDto) {
			return new HarvestProduct();
		} else if (dto instanceof TransformedProductDto) {
			return new TransformedProduct();
		}
		throw new IllegalArgumentException("Type de ProductDto non supporté : " + dto.getClass());
	}

	/**
	 * Méthode factory pour créer une instance concrète de ProductDto lors du mapping depuis Product vers ProductDto.
	 */
	@ObjectFactory
	default ProductDto createDto(Product product) {
		if (product instanceof HarvestProduct) {
			return new HarvestProductDto();
		} else if (product instanceof TransformedProduct) {
			return new TransformedProductDto();
		}
		throw new IllegalArgumentException("Type de Product non supporté : " + product.getClass());
	}
}
