package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.product.HarvestProductDto;
import be.labil.anacarde.domain.dto.db.product.ProductDto;
import be.labil.anacarde.domain.dto.db.product.TransformedProductDto;
import be.labil.anacarde.domain.dto.write.product.HarvestProductUpdateDto;
import be.labil.anacarde.domain.dto.write.product.ProductUpdateDto;
import be.labil.anacarde.domain.dto.write.product.TransformedProductUpdateDto;
import be.labil.anacarde.domain.model.*;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {MapperHelpers.class, DocumentMapper.class,
		UserDetailMapper.class, StoreMapper.class, FieldMapper.class})
public abstract class ProductMapper {

	@Autowired
	protected EntityManager em;

	/*------------------------------------*/
	/* Conversion DTO -> Entité par type */
	/*------------------------------------*/

	// Mapping vers l'entité
	@Mapping(target = "store", ignore = true)
	@Mapping(target = "qualityControl", ignore = true)
	@Mapping(target = "producer", ignore = true)
	@Mapping(target = "field", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "transformedProduct", ignore = true)
	public abstract HarvestProduct toEntity(HarvestProductUpdateDto dto);

	// Mapping vers l'entité
	@Mapping(source = "identifier", target = "identifier")
	@Mapping(target = "store", ignore = true)
	@Mapping(target = "qualityControl", ignore = true)
	@Mapping(target = "transformer", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "harvestProducts", ignore = true)
	public abstract TransformedProduct toEntity(TransformedProductUpdateDto dto);

	@AfterMapping
	protected void afterUpdateDto(ProductUpdateDto dto, @MappingTarget Product product) {
		if (dto.getStoreId() != null) {
			product.setStore(em.getReference(Store.class, dto.getStoreId()));
		}
		if (dto.getQualityControlId() != null) {
			QualityControl qc = em.getReference(QualityControl.class, dto.getQualityControlId());
			product.setQualityControl(qc);
		}
		if (dto instanceof HarvestProductUpdateDto harvest) {
			if (harvest.getProducerId() != null) {
				Producer producer = em.getReference(Producer.class, harvest.getProducerId());
				((HarvestProduct) product).setProducer(producer);
			}
			if (harvest.getFieldId() != null) {
				Field field = em.getReference(Field.class, harvest.getFieldId());
				((HarvestProduct) product).setField(field);
			}
		} else if (dto instanceof TransformedProductUpdateDto transformed) {
			if (transformed.getTransformerId() != null) {
				Transformer transformer = em.getReference(Transformer.class,
						transformed.getTransformerId());
				((TransformedProduct) product).setTransformer(transformer);
			}
			if (transformed.getHarvestProductIds() != null) {
				List<HarvestProduct> harvestProducts = new ArrayList<>();
				for (Integer harvestProductId : transformed.getHarvestProductIds()) {
					HarvestProduct harvestProduct = em.getReference(HarvestProduct.class,
							harvestProductId);
					harvestProducts.add(harvestProduct);
				}
				((TransformedProduct) product).setHarvestProducts(harvestProducts);
			}
		} else {
			throw new IllegalArgumentException(
					"Type de produit non supporté : " + dto.getClass().getName());
		}
	}

	public Product toEntity(ProductUpdateDto dto) {
		if (dto instanceof HarvestProductUpdateDto harvest) {
			return toEntity(harvest);
		} else if (dto instanceof TransformedProductUpdateDto transformed) {
			return toEntity(transformed);
		}
		throw new IllegalArgumentException(
				"Type de DTO non supporté : " + dto.getClass().getName());
	}

	/*------------------------------------*/
	/* Conversion Entité -> DTO par type */
	/*------------------------------------*/

	// Mapping inverse vers le DTO
	@Mapping(source = "store", target = "store")
	@Mapping(source = "producer", target = "producer")
	@Mapping(source = "field", target = "field")
	@Mapping(source = "qualityControl", target = "qualityControl")
	@Mapping(source = "transformedProduct", target = "transformedProduct")
	public abstract HarvestProductDto toDto(HarvestProduct entity);

	// Mapping inverse vers le DTO
	@Mapping(source = "store", target = "store")
	@Mapping(source = "identifier", target = "identifier")
	@Mapping(source = "transformer", target = "transformer")
	@Mapping(source = "qualityControl", target = "qualityControl")
	@Mapping(source = "harvestProducts", target = "harvestProducts")
	public abstract TransformedProductDto toDto(TransformedProduct entity);

	public ProductDto toDto(Product entity) {
		if (entity instanceof HarvestProduct harvest) {
			return toDto(harvest);
		} else if (entity instanceof TransformedProduct transformed) {
			return toDto(transformed);
		}
		throw new IllegalArgumentException(
				"Type de Produit non supporté : " + entity.getClass().getName());
	}

	/*------------------------------------------*/
	/* 3) Mise à jour partielle (partialUpdate) */
	/*------------------------------------------*/

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "store", ignore = true)
	@Mapping(target = "producer", ignore = true)
	@Mapping(target = "field", ignore = true)
	@Mapping(target = "qualityControl", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "transformedProduct", ignore = true)
	public abstract HarvestProduct partialUpdate(HarvestProductUpdateDto dto,
			@MappingTarget HarvestProduct entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "identifier", target = "identifier")
	@Mapping(target = "store", ignore = true)
	@Mapping(target = "qualityControl", ignore = true)
	@Mapping(target = "transformer", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "harvestProducts", ignore = true)
	public abstract TransformedProduct partialUpdate(TransformedProductUpdateDto dto,
			@MappingTarget TransformedProduct entity);

	public Product partialUpdate(ProductUpdateDto dto, @MappingTarget Product entity) {
		if (dto instanceof HarvestProductUpdateDto harvest && entity instanceof HarvestProduct) {
			return partialUpdate(harvest, (HarvestProduct) entity);
		} else
			if (dto instanceof TransformedProductUpdateDto transformed
					&& entity instanceof TransformedProduct) {
						return partialUpdate(transformed, (TransformedProduct) entity);
					}
		throw new IllegalArgumentException(
				"Type de DTO non supporté : " + dto.getClass().getName());
	}
}
