package be.labil.anacarde.domain.mapper;

import static org.junit.jupiter.api.Assertions.*;

import be.labil.anacarde.domain.dto.AuctionOptionDto;
import be.labil.anacarde.domain.model.AuctionOption;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuctionOptionMapperTest {

	@Autowired
	private AuctionOptionMapper auctionOptionMapper;

	@Test
	public void testToDto() {
		AuctionOption auctionOption = new AuctionOption();
		auctionOption.setId(1);
		auctionOption.setName("Option A");

		AuctionOptionDto auctionOptionDto = auctionOptionMapper.toDto(auctionOption);

		assertNotNull(auctionOptionDto);
		assertEquals(auctionOption.getId(), auctionOptionDto.getId());
		assertEquals(auctionOption.getName(), auctionOptionDto.getName());
	}

	@Test
	public void testToEntity() {
		AuctionOptionDto auctionOptionDto = new AuctionOptionDto();
		auctionOptionDto.setId(1);
		auctionOptionDto.setName("Option A");

		AuctionOption auctionOption = auctionOptionMapper.toEntity(auctionOptionDto);

		assertNotNull(auctionOption);
		assertEquals(auctionOptionDto.getId(), auctionOption.getId());
		assertEquals(auctionOptionDto.getName(), auctionOption.getName());
	}
}
