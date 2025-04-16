package be.labil.anacarde.domain.mapper;

import static org.junit.jupiter.api.Assertions.*;

import be.labil.anacarde.domain.dto.AuctionOptionDto;
import be.labil.anacarde.domain.dto.AuctionOptionValueDto;
import be.labil.anacarde.domain.model.Auction;
import be.labil.anacarde.domain.model.AuctionOption;
import be.labil.anacarde.domain.model.AuctionOptionValue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuctionOptionValueMapperTest {

	@Autowired
	private AuctionOptionValueMapper auctionOptionValueMapper;

	@Test
	public void testToDto() {
		AuctionOptionValue auctionOptionValue = new AuctionOptionValue();
		auctionOptionValue.setId(1);

		Auction auction = new Auction();
		auction.setId(1);
		AuctionOption auctionOption = new AuctionOption();
		auctionOption.setId(1);
		auctionOption.setName("Option A");

		auctionOptionValue.setAuction(auction);
		auctionOptionValue.setAuctionOption(auctionOption);
		auctionOptionValue.setOptionValue("Extra option");

		AuctionOptionValueDto auctionOptionValueDto = auctionOptionValueMapper.toDto(auctionOptionValue);

		assertNotNull(auctionOptionValueDto);
		assertEquals(auctionOptionValue.getId(), auctionOptionValueDto.getId());
		assertEquals(auctionOptionValue.getOptionValue(), auctionOptionValueDto.getOptionValue());
		assertNotNull(auctionOptionValueDto.getAuctionOption());
		assertEquals(auctionOptionValue.getAuctionOption().getId(), auctionOptionValueDto.getAuctionOption().getId());
	}

	@Test
	public void testToEntity() {
		AuctionOptionValueDto auctionOptionValueDto = new AuctionOptionValueDto();
		auctionOptionValueDto.setId(1);
		auctionOptionValueDto.setOptionValue("Extra option");

		AuctionOptionDto auctionOptionDto = new AuctionOptionDto();
		auctionOptionDto.setId(1);
		auctionOptionDto.setName("Option A");

		auctionOptionValueDto.setAuctionOption(auctionOptionDto);

		AuctionOptionValue auctionOptionValue = auctionOptionValueMapper.toEntity(auctionOptionValueDto);

		assertNotNull(auctionOptionValue);
		assertEquals(auctionOptionValueDto.getId(), auctionOptionValue.getId());
		assertEquals(auctionOptionValueDto.getOptionValue(), auctionOptionValue.getOptionValue());
		assertNotNull(auctionOptionValue.getAuctionOption());
		assertEquals(auctionOptionDto.getId(), auctionOptionValue.getAuctionOption().getId());
	}
}
