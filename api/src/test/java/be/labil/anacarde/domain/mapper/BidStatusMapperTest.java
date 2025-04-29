package be.labil.anacarde.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import be.labil.anacarde.domain.dto.BidStatusDto;
import be.labil.anacarde.domain.model.BidStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BidStatusMapperTest {

	@Autowired
	private BidStatusMapper bidStatusMapper;

	@Test
	void testToDto() {
		BidStatus entity = new BidStatus();
		entity.setId(1);
		entity.setName("Accepté");

		BidStatusDto dto = bidStatusMapper.toDto(entity);

		assertNotNull(dto);
		assertEquals(entity.getId(), dto.getId());
		assertEquals(entity.getName(), dto.getName());
	}

	@Test
	void testToEntity() {
		BidStatusDto dto = new BidStatusDto();
		dto.setId(2);
		dto.setName("Rejeté");

		BidStatus entity = bidStatusMapper.toEntity(dto);

		assertNotNull(entity);
		assertEquals(dto.getId(), entity.getId());
		assertEquals(dto.getName(), entity.getName());
	}
}
