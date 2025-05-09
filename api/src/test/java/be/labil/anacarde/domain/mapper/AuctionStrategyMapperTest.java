package be.labil.anacarde.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import be.labil.anacarde.domain.dto.db.AuctionStrategyDto;
import be.labil.anacarde.domain.model.AuctionStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AuctionStrategyMapperTest {

	@Autowired
	private AuctionStrategyMapper auctionStrategyMapper;

	@Test
	void testToDto() {
		AuctionStrategy entity = new AuctionStrategy();
		entity.setId(1);
		entity.setName("Stratégie Standard");

		AuctionStrategyDto dto = auctionStrategyMapper.toDto(entity);

		assertNotNull(dto);
		assertEquals(entity.getId(), dto.getId());
		assertEquals(entity.getName(), dto.getName());
	}

	@Test
	void testToEntity() {
		AuctionStrategyDto dto = new AuctionStrategyDto();
		dto.setId(2);
		dto.setName("Stratégie Avancée");

		AuctionStrategy entity = auctionStrategyMapper.toEntity(dto);

		assertNotNull(entity);
		assertEquals(dto.getId(), entity.getId());
		assertEquals(dto.getName(), entity.getName());
	}
}
