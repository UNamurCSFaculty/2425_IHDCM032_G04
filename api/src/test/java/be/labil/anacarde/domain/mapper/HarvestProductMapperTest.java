package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import be.labil.anacarde.domain.dto.db.product.HarvestProductDto;
import be.labil.anacarde.domain.dto.write.product.HarvestProductUpdateDto;
import be.labil.anacarde.domain.model.Field;
import be.labil.anacarde.domain.model.HarvestProduct;
import be.labil.anacarde.domain.model.Producer;
import be.labil.anacarde.domain.model.Store;
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

		HarvestProduct entity = HarvestProduct.builder().id(1).store(store).producer(producer)
				.field(Field.builder().id(30).identifier("F002").build()).build();

		HarvestProductDto dto = mapper.toDto(entity);

		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(1);

		assertThat(dto.getStore()).isNotNull();
		assertThat(dto.getStore().getId()).isEqualTo(10);

		assertThat(dto.getProducer()).isNotNull();
		assertThat(dto.getProducer().getId()).isEqualTo(20);

		assertThat(dto.getField()).isNotNull();
		assertThat(dto.getField().getId()).isEqualTo(30);
	}

	@Test
	void shouldMapDtoToEntity() {
		HarvestProductUpdateDto dto = new HarvestProductUpdateDto();
		dto.setStoreId(1);
		dto.setProducerId(2);
		dto.setFieldId(3);

		HarvestProduct entity = mapper.toEntity(dto);

		assertThat(entity.getStore().getId()).isEqualTo(1);
		assertThat(entity.getProducer().getId()).isEqualTo(2);
		assertThat(entity.getField().getId()).isEqualTo(3);
	}
}
