package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.ProductDto;
import be.labil.anacarde.domain.mapper.ProductMapper;
import be.labil.anacarde.domain.model.HarvestProduct;
import be.labil.anacarde.domain.model.Product;
import be.labil.anacarde.domain.model.TransformedProduct;
import be.labil.anacarde.infrastructure.persistence.HarvestProductRepository;
import be.labil.anacarde.infrastructure.persistence.ProductRepository;
import be.labil.anacarde.infrastructure.persistence.TransformedProductRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
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
	// private final QualityControlRepository qualityControlRepository;

	@Override
	public ProductDto createProduct(ProductDto dto) {
		Product product = productMapper.toEntity(dto);

		/*
		 * A ajouter une le repo qualityControl dispo if (dto.getQualityControlId() != null) { QualityControl qc =
		 * qualityControlRepository.findById(dto.getQualityControlId()) .orElseThrow(() -> new
		 * ResourceNotFoundException( "QualityControl non trouvé pour id " + dto.getQualityControlId()));
		 * product.setQualityControl(qc); }
		 */

		Product saved = productRepository.save(product);
		return productMapper.toDto(saved);
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
	public List<ProductDto> listProducts(Integer traderId) {
		if (traderId != null) {
			List<HarvestProduct> harvests = harvestProductRepository.findByProducerId(traderId);
			List<TransformedProduct> transformed = transformedRepository.findByTransformerId(traderId);
			return Stream
					.concat(harvests.stream().map(productMapper::toDto), transformed.stream().map(productMapper::toDto))
					.collect(Collectors.toList());
		} else {
			return productRepository.findAll().stream().map(productMapper::toDto).collect(Collectors.toList());
		}
	}

	@Override
	public ProductDto updateProduct(Integer id, ProductDto productDto) {
		Product existingProduct = productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

		Product updatedProduct = productMapper.partialUpdate(productDto, existingProduct);

		Product saved = productRepository.save(updatedProduct);
		return productMapper.toDto(saved);
	}

	@Override
	public void deleteProduct(Integer id) {
		if (!productRepository.existsById(id)) {
			throw new ResourceNotFoundException("Produit non trouvé");
		}
		productRepository.deleteById(id);
	}
}
