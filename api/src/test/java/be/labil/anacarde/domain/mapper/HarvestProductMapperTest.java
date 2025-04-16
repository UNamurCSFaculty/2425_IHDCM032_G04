package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import be.labil.anacarde.domain.dto.*;
import be.labil.anacarde.domain.dto.user.ProducerDetailDto;
import be.labil.anacarde.domain.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HarvestProductMapperTest {

	@Autowired
	private ProductMapper mapper;

	@Test
	void shouldMapEntityToDto() {
		Producer producer = new Producer();
		producer.setId(20);
		Store store = new Store();
		store.setId(10);
		TransformedProduct transformedProduct = new TransformedProduct();
		transformedProduct.setId(40);
		HarvestProduct entity = HarvestProduct.builder().id(1).store(store).producer(producer)
				.field(new Field(30, "F001", null, null)).transformedProduct(transformedProduct).build();

		HarvestProductDto dto = mapper.toDto(entity);

		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(1);

		assertThat(dto.getStore()).isNotNull();
		assertThat(dto.getStore().getId()).isEqualTo(10);

		assertThat(dto.getProducer()).isNotNull();
		assertThat(dto.getProducer().getId()).isEqualTo(20);

		assertThat(dto.getField()).isNotNull();
		assertThat(dto.getField().getId()).isEqualTo(30);

		assertThat(dto.getTransformedProduct()).isNotNull();
		assertThat(dto.getTransformedProduct().getId()).isEqualTo(40);
	}

	@Test
	void shouldMapDtoToEntity() {
		HarvestProductDto dto = new HarvestProductDto();
		dto.setId(1);

		StoreDetailDto store = new StoreDetailDto();
		store.setId(100);
		dto.setStore(store);

		ProducerDetailDto producer = new ProducerDetailDto();
		producer.setId(200);
		dto.setProducer(producer);

		FieldDetailDto field = new FieldDetailDto();
		field.setId(300);
		dto.setField(field);

		TransformedProductDto transformed = new TransformedProductDto();
		transformed.setId(400);
		dto.setTransformedProduct(transformed);

		HarvestProduct entity = mapper.toEntity(dto);

		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isEqualTo(1);

		assertThat(entity.getStore()).isNotNull();
		assertThat(entity.getStore().getId()).isEqualTo(100);

		assertThat(entity.getProducer()).isNotNull();
		assertThat(entity.getProducer().getId()).isEqualTo(200);

		assertThat(entity.getField()).isNotNull();
		assertThat(entity.getField().getId()).isEqualTo(300);

		assertThat(entity.getTransformedProduct()).isNotNull();
		assertThat(entity.getTransformedProduct().getId()).isEqualTo(400);
	}

	@Test
	void shouldHandleNullTransformedProduct() {
		Producer producer = new Producer();
		producer.setId(1);
		Store store = new Store();
		store.setId(1);
		HarvestProduct entity = HarvestProduct.builder().id(2).store(store).producer(producer)
				.field(new Field(3, "F002", null, null)).transformedProduct(null).build();

		HarvestProductDto dto = mapper.toDto(entity);

		assertThat(dto.getTransformedProduct()).isNull();
	}
}
