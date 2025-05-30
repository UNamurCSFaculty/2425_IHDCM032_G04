package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ApiErrorCode;
import be.labil.anacarde.application.exception.ApiErrorException;
import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.db.product.ProductDto;
import be.labil.anacarde.domain.dto.write.product.ProductUpdateDto;
import be.labil.anacarde.domain.dto.write.product.TransformedProductUpdateDto;
import be.labil.anacarde.domain.mapper.ProductMapper;
import be.labil.anacarde.domain.model.HarvestProduct;
import be.labil.anacarde.domain.model.Product;
import be.labil.anacarde.domain.model.TransformedProduct;
import be.labil.anacarde.infrastructure.persistence.HarvestProductRepository;
import be.labil.anacarde.infrastructure.persistence.ProductRepository;
import be.labil.anacarde.infrastructure.persistence.TransformedProductRepository;
import be.labil.anacarde.infrastructure.util.PersistenceHelper;
import be.labil.anacarde.presentation.controller.enums.ProductType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
	private final HarvestProductRepository harvestProductRepository;
	private final TransformedProductRepository transformedRepository;
	private final ProductRepository productRepository;
	private final ProductMapper productMapper;
	private final PersistenceHelper persistenceHelper;

	@Override
	public ProductDto createProduct(ProductUpdateDto dto) {
		Product product = productMapper.toEntity(dto);
		if (dto instanceof TransformedProductUpdateDto tpDto
				&& product instanceof TransformedProduct tp) {
			List<HarvestProduct> harvests = harvestProductRepository
					.findAllById(tpDto.getHarvestProductIds());
			for (HarvestProduct hp : harvests) {
				hp.setTransformedProduct(tp);
			}
			tp.setHarvestProducts(harvests);
		}
		Product full = persistenceHelper.saveAndReload(productRepository, product, Product::getId);
		return productMapper.toDto(full);
	}

	@Override
	@Transactional(readOnly = true)
	public ProductDto getProductById(Integer id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
		return productMapper.toDto(product);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductDto> listProducts(Integer traderId, ProductType productType) {
		List<HarvestProduct> harvestList = new ArrayList<>();
		List<TransformedProduct> transformedList = new ArrayList<>();

		if (productType == null) {
			harvestList = harvestProductRepository.findByProducerId(traderId);
			transformedList = transformedRepository.findByTransformerId(traderId);
		} else {
			switch (productType) {
				case harvest -> harvestList = harvestProductRepository.findByProducerId(traderId);
				case transformed ->
					transformedList = transformedRepository.findByTransformerId(traderId);
				default -> throw new ResourceNotFoundException("ProductType not found");
			}
		}

		return Stream
				.concat(harvestList.stream().map(productMapper::toDto),
						transformedList.stream().map(productMapper::toDto))
				.collect(Collectors.toList());
	}

	@Override
	public ProductDto updateProduct(Integer id, ProductUpdateDto productDto) {
		Product existingProduct = productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

		Product updatedProduct = productMapper.partialUpdate(productDto, existingProduct);

		Product full = persistenceHelper.saveAndReload(productRepository, updatedProduct,
				Product::getId);
		return productMapper.toDto(full);
	}

	@Override
	public void deleteProduct(Integer id) {
		if (!productRepository.existsById(id)) {
			throw new ResourceNotFoundException("Produit non trouvé");
		}
		productRepository.deleteById(id);
	}

	@Override
	public void offsetWeightKgAvailable(Integer productId, double offset) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

		double newWeight = product.getWeightKgAvailable() + offset;
		if (newWeight < 0) {
			throw new ApiErrorException(HttpStatus.BAD_REQUEST, ApiErrorCode.BAD_REQUEST.code(),
					"weightKgAvailable", "Quantité disponible insuffisante pour le produit");
		}

		product.setWeightKgAvailable(newWeight);
		productRepository.save(product);
	}
}
