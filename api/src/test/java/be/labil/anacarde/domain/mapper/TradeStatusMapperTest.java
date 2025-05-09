package be.labil.anacarde.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import be.labil.anacarde.domain.dto.db.TradeStatusDto;
import be.labil.anacarde.domain.model.TradeStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TradeStatusMapperTest {

	@Autowired
	private TradeStatusMapper TradeStatusMapper;

	@Test
	void testToDto() {
		TradeStatus entity = new TradeStatus();
		entity.setId(1);
		entity.setName("Accepté");

		TradeStatusDto dto = TradeStatusMapper.toDto(entity);

		assertNotNull(dto);
		assertEquals(entity.getId(), dto.getId());
		assertEquals(entity.getName(), dto.getName());
	}

	@Test
	void testToEntity() {
		TradeStatusDto dto = new TradeStatusDto();
		dto.setId(2);
		dto.setName("Rejeté");

		TradeStatus entity = TradeStatusMapper.toEntity(dto);

		assertNotNull(entity);
		assertEquals(dto.getId(), entity.getId());
		assertEquals(dto.getName(), entity.getName());
	}
}
