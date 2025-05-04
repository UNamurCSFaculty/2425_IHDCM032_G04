package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import be.labil.anacarde.domain.dto.StoreDetailDto;
import be.labil.anacarde.domain.model.Store;
import be.labil.anacarde.domain.model.Transformer;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StoreMapperTest {

	@Autowired
	private StoreMapper storeMapper;

	@Test
	void shouldMapEntityToDto() {
		Point location = new GeometryFactory().createPoint(new org.locationtech.jts.geom.Coordinate(2.3522, 48.8566));
		Store store = new Store();
		store.setId(1);
		store.setName("Nassara");
		store.setLocation(location);
		store.setUser(new Transformer());
		store.getUser().setId(42);

		StoreDetailDto dto = storeMapper.toDto(store);

		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(store.getId());
		assertThat(dto.getName()).isEqualTo("Nassara");
		assertThat(dto.getLocation()).isEqualTo("POINT (2.3522 48.8566)"); // WKT representation
		assertThat(dto.getUserId()).isEqualTo(store.getUser().getId());
	}

	@Test
	void shouldMapDtoToEntity() {
		StoreDetailDto dto = new StoreDetailDto();
		dto.setId(1);
		dto.setName("Nassara");
		dto.setLocation("POINT (2.3522 48.8566)");
		dto.setUserId(42);

		Store entity = storeMapper.toEntity(dto);

		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isEqualTo(dto.getId());
		assertThat(entity.getName()).isEqualTo("Nassara");
		assertThat(entity.getLocation().getX()).isEqualTo(2.3522);
		assertThat(entity.getLocation().getY()).isEqualTo(48.8566);
		assertThat(entity.getUser().getId()).isEqualTo(dto.getUserId());
	}

	@Test
	void shouldHandleNullValuesInDto() {
		StoreDetailDto dto = new StoreDetailDto();
		dto.setName(null);
		dto.setId(null);
		dto.setLocation(null);
		dto.setUserId(null);

		Store entity = storeMapper.toEntity(dto);

		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isNull();
		assertThat(entity.getName()).isNull();
		assertThat(entity.getLocation()).isNull();
		assertThat(entity.getUser()).isNull();
	}

	@Test
	void shouldHandleNullValuesInEntity() {
		Store store = new Store();
		store.setId(null);
		store.setName(null);
		store.setLocation(null);
		store.setUser(null);

		StoreDetailDto dto = storeMapper.toDto(store);

		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isNull();
		assertThat(dto.getName()).isNull();
		assertThat(dto.getLocation()).isNull();
		assertThat(dto.getUserId()).isNull();
	}

	@Test
	void shouldMapPartialUpdate() {
		StoreDetailDto dto = new StoreDetailDto();
		dto.setId(1);
		dto.setName("Nassara");
		dto.setLocation("POINT (3.3522 49.8566)");
		dto.setUserId(42);

		Store entity = new Store();
		entity.setId(1);
		entity.setLocation(
				new GeometryFactory().createPoint(new org.locationtech.jts.geom.Coordinate(2.3522, 48.8566)));
		entity.setUser(new Transformer());
		entity.getUser().setId(41);

		storeMapper.partialUpdate(dto, entity);

		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isEqualTo(dto.getId());
		assertThat(entity.getName()).isEqualTo(dto.getName());
		assertThat(entity.getLocation().getX()).isEqualTo(3.3522);
		assertThat(entity.getLocation().getY()).isEqualTo(49.8566);
		assertThat(entity.getUser().getId()).isEqualTo(42); // User ID should be updated
	}
}
