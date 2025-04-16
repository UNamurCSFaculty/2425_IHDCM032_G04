package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.*;

import be.labil.anacarde.domain.dto.FieldDetailDto;
import be.labil.anacarde.domain.dto.user.ProducerDetailDto;
import be.labil.anacarde.domain.model.Field;
import be.labil.anacarde.domain.model.Producer;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FieldDetailMapperTest {

	@Autowired
	private FieldDetailMapper mapper;

	private final GeometryFactory geometryFactory = new GeometryFactory();

	@Test
	void shouldMapEntityToDto() {
		Producer producer = new Producer();
		producer.setId(1);

		Point location = geometryFactory.createPoint(new Coordinate(2.35, 48.85));

		Field field = new Field();
		field.setId(100);
		field.setIdentifier("F123");
		field.setLocation(location);
		field.setProducer(producer);

		FieldDetailDto dto = mapper.toDto(field);

		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(100);
		assertThat(dto.getIdentifier()).isEqualTo("F123");
		assertThat(dto.getLocation()).isEqualTo("POINT (2.35 48.85)");
		assertThat(dto.getProducer()).isNotNull();
		assertThat(dto.getProducer().getId()).isEqualTo(1);
	}

	@Test
	void shouldMapDtoToEntity() {
		ProducerDetailDto producerDto = new ProducerDetailDto();
		producerDto.setId(1);

		FieldDetailDto dto = new FieldDetailDto();
		dto.setId(100);
		dto.setIdentifier("F123");
		dto.setLocation("POINT(2.35 48.85)");
		dto.setProducer(producerDto);

		Field entity = mapper.toEntity(dto);

		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isEqualTo(100);
		assertThat(entity.getIdentifier()).isEqualTo("F123");
		assertThat(entity.getLocation()).isNotNull();
		assertThat(entity.getLocation().getX()).isEqualTo(2.35);
		assertThat(entity.getLocation().getY()).isEqualTo(48.85);
		assertThat(entity.getProducer()).isNotNull();
		assertThat(entity.getProducer().getId()).isEqualTo(1);
	}

	@Test
	void shouldHandleNullLocation() {
		FieldDetailDto dto = new FieldDetailDto();
		dto.setLocation(null);

		Field entity = mapper.toEntity(dto);

		assertThat(entity.getLocation()).isNull();
	}

	@Test
	void shouldFailOnInvalidWKT() {
		FieldDetailDto dto = new FieldDetailDto();
		dto.setLocation("INVALID_WKT");

		assertThatThrownBy(() -> mapper.toEntity(dto)).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Format WKT invalide");
	}
}
