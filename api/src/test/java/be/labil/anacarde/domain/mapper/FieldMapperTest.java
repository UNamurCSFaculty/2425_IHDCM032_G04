package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import be.labil.anacarde.domain.dto.db.FieldDto;
import be.labil.anacarde.domain.dto.db.user.ProducerDetailDto;
import be.labil.anacarde.domain.model.Field;
import be.labil.anacarde.domain.model.Producer;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
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

		Point location = geometryFactory.createPoint(new Coordinate(2.35, 48.85));

		Field field = new Field();
		field.setId(100);
		field.setIdentifier("F123");
		field.setLocation(location);
		field.setProducer(producer);

		FieldDto dto = mapper.toDto(field);

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

		FieldDto dto = new FieldDto();
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
		FieldDto dto = new FieldDto();
		dto.setLocation(null);

		Field entity = mapper.toEntity(dto);

		assertThat(entity.getLocation()).isNull();
	}

	@Test
	void shouldFailOnInvalidWKT() {
		FieldDto dto = new FieldDto();
		dto.setLocation("INVALID_WKT");

		assertThatThrownBy(() -> mapper.toEntity(dto)).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Format WKT invalide");
	}
}
