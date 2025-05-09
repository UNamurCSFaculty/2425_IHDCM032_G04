package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import be.labil.anacarde.domain.dto.db.product.TransformedProductDto;
import be.labil.anacarde.domain.dto.write.product.TransformedProductUpdateDto;
import be.labil.anacarde.domain.model.TransformedProduct;
import be.labil.anacarde.domain.model.Transformer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TransformedProductMapperTest {

	@Autowired
	private ProductMapper mapper;

	@Test
	void testToDto() {
		Transformer transformer = new Transformer();
		transformer.setId(1);
		transformer.setFirstName("Transformer One");

		TransformedProduct product = TransformedProduct.builder().identifier("TP001")
				.transformer(transformer).build();

		TransformedProductDto dto = mapper.toDto(product);

		assertNotNull(dto);
		assertEquals("TP001", dto.getIdentifier());
		assertNotNull(dto.getTransformer());
		assertEquals("Transformer One", dto.getTransformer().getFirstName());
	}

	@Test
	void testToEntity() {
		TransformedProductUpdateDto dto = new TransformedProductUpdateDto();
		dto.setStoreId(1);
		dto.setTransformerId(2);
		dto.setIdentifier("TP001");

		TransformedProduct entity = mapper.toEntity(dto);

		assertThat(entity).isNotNull();

		assertThat(entity.getStore().getId()).isEqualTo(1);
		assertThat(entity.getTransformer().getId()).isEqualTo(2);
		assertThat(entity.getIdentifier()).isEqualTo("TP001");
	}

	@Test
	void testPartialUpdate() {
		Transformer transformer = new Transformer();
		transformer.setId(1);
		transformer.setFirstName("Transformer One");

		TransformedProduct existingProduct = TransformedProduct.builder().identifier("TP001")
				.transformer(transformer).build();

		TransformedProductUpdateDto dto = new TransformedProductUpdateDto();
		dto.setIdentifier("TP002");
		dto.setTransformerId(2);

		mapper.partialUpdate(dto, existingProduct);

		assertNotNull(existingProduct);
		assertEquals("TP002", existingProduct.getIdentifier());
		assertNotNull(existingProduct.getTransformer());
		assertEquals(2, existingProduct.getTransformer().getId());
	}
}
