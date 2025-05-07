package be.labil.anacarde.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import be.labil.anacarde.domain.dto.TransformedProductDto;
import be.labil.anacarde.domain.dto.user.TransformerDetailDto;
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

		TransformedProduct product = TransformedProduct.builder().identifier("TP001").transformer(transformer).build();

		TransformedProductDto dto = mapper.toDto(product);

		assertNotNull(dto);
		assertEquals("TP001", dto.getIdentifier());
		assertNotNull(dto.getTransformer());
		assertEquals("Transformer One", dto.getTransformer().getFirstName());
	}

	@Test
	void testToEntity() {
		TransformerDetailDto transformerDto = new TransformerDetailDto();
		transformerDto.setId(1);
		transformerDto.setFirstName("Transformer One");

		TransformedProductDto dto = new TransformedProductDto();
		dto.setIdentifier("TP001");
		dto.setTransformer(transformerDto);

		TransformedProduct product = mapper.toEntity(dto);

		assertNotNull(product);
		assertEquals("TP001", product.getIdentifier());
		assertNotNull(product.getTransformer());
		assertEquals("Transformer One", product.getTransformer().getFirstName());
	}

	@Test
	void testPartialUpdate() {
		Transformer transformer = new Transformer();
		transformer.setId(1);
		transformer.setFirstName("Transformer One");

		TransformedProduct existingProduct = TransformedProduct.builder().identifier("TP001").transformer(transformer)
				.build();

		TransformerDetailDto transformerDto = new TransformerDetailDto();
		transformerDto.setId(2);
		transformerDto.setFirstName("Transformer Two");

		TransformedProductDto dto = new TransformedProductDto();
		dto.setIdentifier("TP002");
		dto.setTransformer(transformerDto);

		mapper.partialUpdate(dto, existingProduct);

		assertNotNull(existingProduct);
		assertEquals("TP002", existingProduct.getIdentifier());
		assertNotNull(existingProduct.getTransformer());
		assertEquals("Transformer Two", existingProduct.getTransformer().getFirstName());
	}
}
