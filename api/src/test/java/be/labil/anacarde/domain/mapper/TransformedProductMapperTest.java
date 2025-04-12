package be.labil.anacarde.domain.mapper;

import static org.junit.jupiter.api.Assertions.*;

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
	private TransformedProductMapper mapper;

	@Test
	void testToDto() {
		Transformer transformer = new Transformer();
		transformer.setId(1);
		transformer.setFirstName("Transformer One");

		TransformedProduct product = TransformedProduct.builder().identifier("TP001").location("Zone A")
				.transformer(transformer).build();

		TransformedProductDto dto = mapper.toDto(product);

		assertNotNull(dto);
		assertEquals("TP001", dto.getIdentifier());
		assertEquals("Zone A", dto.getLocation());
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
		dto.setLocation("Zone A");
		dto.setTransformer(transformerDto);

		TransformedProduct product = mapper.toEntity(dto);

		assertNotNull(product);
		assertEquals("TP001", product.getIdentifier());
		assertEquals("Zone A", product.getLocation());
		assertNotNull(product.getTransformer());
		assertEquals("Transformer One", product.getTransformer().getFirstName());
	}

	@Test
	void testPartialUpdate() {
		Transformer transformer = new Transformer();
		transformer.setId(1);
		transformer.setFirstName("Transformer One");

		TransformedProduct existingProduct = TransformedProduct.builder().identifier("TP001").location("Old Zone")
				.transformer(transformer).build();

		TransformerDetailDto transformerDto = new TransformerDetailDto();
		transformerDto.setId(2);
		transformerDto.setFirstName("Transformer Two");

		TransformedProductDto dto = new TransformedProductDto();
		dto.setIdentifier("TP002");
		dto.setLocation("Zone B");
		dto.setTransformer(transformerDto);

		mapper.partialUpdate(dto, existingProduct);

		assertNotNull(existingProduct);
		assertEquals("TP002", existingProduct.getIdentifier());
		assertEquals("Zone B", existingProduct.getLocation());
		assertNotNull(existingProduct.getTransformer());
		assertEquals("Transformer Two", existingProduct.getTransformer().getFirstName());
	}
}
