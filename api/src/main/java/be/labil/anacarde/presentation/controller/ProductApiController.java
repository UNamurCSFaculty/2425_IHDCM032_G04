package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.ProductService;
import be.labil.anacarde.domain.dto.db.product.ProductDto;
import be.labil.anacarde.domain.dto.write.product.ProductUpdateDto;
import be.labil.anacarde.presentation.controller.enums.ProductType;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class ProductApiController implements ProductApi {
	private final ProductService productService;

	@Override
	public ResponseEntity<? extends ProductDto> getProduct(Integer id) {
		ProductDto Product = productService.getProductById(id);
		return ResponseEntity.ok(Product);
	}

	@Override
	public ResponseEntity<List<? extends ProductDto>> listProducts(Integer traderId,
			ProductType productType) {
		List<ProductDto> products = productService.listProducts(traderId, productType);
		return ResponseEntity.ok(products);
	}

	@Override
	public ResponseEntity<ProductDto> createProduct(ProductUpdateDto productDto) {
		ProductDto created = productService.createProduct(productDto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(created.getId()).toUri();
		return ResponseEntity.created(location).body(created);
	}

	@Override
	public ResponseEntity<ProductDto> updateProduct(Integer id, ProductUpdateDto productDto) {
		ProductDto updated = productService.updateProduct(id, productDto);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<Void> deleteProduct(Integer id) {
		productService.deleteProduct(id);
		return ResponseEntity.noContent().build();
	}
}
