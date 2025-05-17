package be.labil.anacarde.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import be.labil.anacarde.domain.dto.db.AuctionOptionsDto;
import be.labil.anacarde.domain.dto.write.AuctionOptionsUpdateDto;
import be.labil.anacarde.domain.model.AuctionOptions;
import be.labil.anacarde.domain.model.AuctionStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuctionOptionMapperTest {

	@Autowired
	private AuctionOptionsMapper auctionOptionsMapper;

	@Test
	public void testToDto() {
		AuctionOptions auctionOption = new AuctionOptions();
		AuctionStrategy auctionStrategy = new AuctionStrategy();
		auctionStrategy.setName("bestOffer");
		auctionOption.setStrategy(auctionStrategy);
		auctionOption.setFixedPriceKg(150.);
		auctionOption.setMaxPriceKg(300.);
		auctionOption.setMinPriceKg(80.);
		auctionOption.setBuyNowPrice(250.);
		auctionOption.setShowPublic(true);

		AuctionOptionsDto auctionOptionDto = auctionOptionsMapper.toDto(auctionOption);

		assertNotNull(auctionOptionDto);
		// TODO:
	}

	@Test
	public void testToEntity() {
		AuctionOptionsUpdateDto auctionOptionsDto = new AuctionOptionsUpdateDto();
		auctionOptionsDto.setStrategyId(1);
		auctionOptionsDto.setFixedPriceKg(150);
		auctionOptionsDto.setMaxPriceKg(300);
		auctionOptionsDto.setMinPriceKg(80);
		auctionOptionsDto.setBuyNowPrice(250);
		auctionOptionsDto.setShowPublic(true);

		AuctionOptions auctionOptions = auctionOptionsMapper.toEntity(auctionOptionsDto);

		assertNotNull(auctionOptions);
		assertEquals(auctionOptionsDto.getStrategyId(), auctionOptions.getStrategy().getId());
		assertEquals(auctionOptionsDto.getFixedPriceKg(), auctionOptions.getFixedPriceKg());
		assertEquals(auctionOptionsDto.getMaxPriceKg(), auctionOptions.getMaxPriceKg());
		assertEquals(auctionOptionsDto.getMinPriceKg(), auctionOptions.getMinPriceKg());
		assertEquals(auctionOptionsDto.getBuyNowPrice(), auctionOptions.getBuyNowPrice());
		assertEquals(auctionOptionsDto.getShowPublic(), auctionOptions.getShowPublic());
	}
}
