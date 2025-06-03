package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import be.labil.anacarde.domain.dto.db.FieldDto;
import be.labil.anacarde.domain.dto.db.user.ProducerDetailDto;
import be.labil.anacarde.domain.dto.write.AddressUpdateDto;
import be.labil.anacarde.domain.dto.write.FieldUpdateDto;
import be.labil.anacarde.domain.model.*;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FieldMapperTest {

	@Autowired
	private FieldMapper mapper;

	private final GeometryFactory geometryFactory = new GeometryFactory();

	@Test
	void shouldMapEntityToDto() {
		Producer producer = new Producer();
		producer.setId(1);

		Region region = Region.builder().name("sud").id(1).build();
		City city = City.builder().name("sud city").id(1).region(region).build();
		Address address = Address.builder().street("Rue de la paix").city(city).region(region)
				.build();

		Field field = new Field();
		field.setId(100);
		field.setIdentifier("F123");
		field.setAddress(address);
		field.setProducer(producer);

		FieldDto dto = mapper.toDto(field);

		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(100);
		assertThat(dto.getIdentifier()).isEqualTo("F123");
		assertThat(dto.getAddress().getStreet()).isEqualTo("Rue de la paix");
		assertThat(dto.getAddress().getCityId()).isEqualTo(1);
		assertThat(dto.getAddress().getRegionId()).isEqualTo(1);
		assertThat(dto.getProducer()).isNotNull();
		assertThat(dto.getProducer().getId()).isEqualTo(1);
	}

	@Test
	void shouldMapDtoToEntity() {
		ProducerDetailDto producerDto = new ProducerDetailDto();
		producerDto.setId(1);

		FieldUpdateDto dto = new FieldUpdateDto();
		dto.setIdentifier("F123");
		dto.setAddress(
				AddressUpdateDto.builder().street("Rue de la paix").cityId(1).regionId(1).build());
		dto.setProducerId(producerDto.getId());

		Field entity = mapper.toEntity(dto);

		assertThat(entity).isNotNull();
		assertThat(entity.getIdentifier()).isEqualTo("F123");
		assertThat(entity.getAddress()).isNotNull();
		assertThat(entity.getProducer()).isNotNull();
		assertThat(entity.getProducer().getId()).isEqualTo(1);
	}
}
