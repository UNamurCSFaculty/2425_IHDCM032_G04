package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import be.labil.anacarde.domain.dto.db.AddressDto;
import be.labil.anacarde.domain.dto.db.StoreDetailDto;
import be.labil.anacarde.domain.model.*;
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
		Point location = new GeometryFactory()
				.createPoint(new org.locationtech.jts.geom.Coordinate(2.3522, 48.8566));
		Store store = new Store();
		store.setId(1);
		store.setName("Nassara");
		Region region = Region.builder().name("sud").id(1).build();
		City city = City.builder().name("sud city").id(1).region(region).build();
		Address address = Address.builder().street("Rue de la paix").city(city).region(region)
				.build();
		store.setAddress(address);
		store.setUser(new Transformer());
		store.getUser().setId(42);

		StoreDetailDto dto = storeMapper.toDto(store);

		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(store.getId());
		assertThat(dto.getName()).isEqualTo("Nassara");
		assertThat(dto.getAddress().getStreet()).isEqualTo("Rue de la paix");
		assertThat(dto.getAddress().getCityId()).isEqualTo(1);
		assertThat(dto.getAddress().getRegionId()).isEqualTo(1);
		assertThat(dto.getUserId()).isEqualTo(store.getUser().getId());
	}

	@Test
	void shouldMapDtoToEntity() {
		StoreDetailDto dto = new StoreDetailDto();
		dto.setId(1);
		dto.setName("Nassara");
		dto.setAddress(AddressDto.builder().street("Rue de la paix").cityId(1).regionId(1).build());
		dto.setUserId(42);

		Store entity = storeMapper.toEntity(dto);

		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isEqualTo(dto.getId());
		assertThat(entity.getName()).isEqualTo("Nassara");
		assertThat(entity.getAddress()).isNotNull();
		assertThat(entity.getUser().getId()).isEqualTo(dto.getUserId());
	}

	@Test
	void shouldHandleNullValuesInDto() {
		StoreDetailDto dto = new StoreDetailDto();
		dto.setName(null);
		dto.setId(null);
		dto.setAddress(null);
		dto.setUserId(null);

		Store entity = storeMapper.toEntity(dto);

		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isNull();
		assertThat(entity.getName()).isNull();
		assertThat(entity.getAddress()).isNull();
		assertThat(entity.getUser()).isNull();
	}

	@Test
	void shouldHandleNullValuesInEntity() {
		Store store = new Store();
		store.setId(null);
		store.setName(null);
		store.setAddress(null);
		store.setUser(null);

		StoreDetailDto dto = storeMapper.toDto(store);

		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isNull();
		assertThat(dto.getName()).isNull();
		assertThat(dto.getAddress()).isNull();
		assertThat(dto.getUserId()).isNull();
	}

	@Test
	void shouldMapPartialUpdate() {
		StoreDetailDto dto = new StoreDetailDto();
		dto.setId(1);
		dto.setName("Nassara");
		dto.setAddress(AddressDto.builder().street("Rue de la paix").cityId(1).regionId(1).build());
		dto.setUserId(42);

		Store entity = new Store();
		entity.setId(1);
		Region region = Region.builder().name("sud").id(1).build();
		City city = City.builder().name("sud city").id(1).region(region).build();
		Address address = Address.builder().street("Rue de la paix").city(city).region(region)
				.build();
		entity.setAddress(address);
		entity.setUser(new Transformer());
		entity.getUser().setId(41);

		storeMapper.partialUpdate(dto, entity);

		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isEqualTo(dto.getId());
		assertThat(entity.getName()).isEqualTo(dto.getName());
		assertThat(dto.getAddress().getStreet()).isEqualTo("Rue de la paix");
		assertThat(dto.getAddress().getCityId()).isEqualTo(1);
		assertThat(dto.getAddress().getRegionId()).isEqualTo(1);
		assertThat(entity.getUser().getId()).isEqualTo(42); // User ID should be updated
	}
}
