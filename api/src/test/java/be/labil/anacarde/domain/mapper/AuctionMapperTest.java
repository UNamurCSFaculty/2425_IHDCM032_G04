package be.labil.anacarde.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import be.labil.anacarde.domain.dto.db.AuctionDto;
import be.labil.anacarde.domain.dto.db.AuctionStrategyDto;
import be.labil.anacarde.domain.dto.write.AuctionOptionsUpdateDto;
import be.labil.anacarde.domain.dto.write.AuctionUpdateDto;
import be.labil.anacarde.domain.model.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuctionMapperTest {

	@Autowired
	private AuctionMapper auctionMapper;

	@Test
	public void testToDto() {
		Auction auction = new Auction();
		auction.setId(1);
		auction.setPrice(100.50);
		auction.setProductQuantity(10);
		auction.setExpirationDate(LocalDateTime.of(2025, 12, 31, 23, 59, 59, 0));
		auction.setCreationDate(LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0));
		auction.setTrader(new Transformer());
		auction.setStatus(new TradeStatus());
		auction.setActive(true);

		AuctionStrategy strategy = new AuctionStrategy();
		strategy.setId(1);
		AuctionOptions options = new AuctionOptions();
		options.setStrategy(strategy);
		options.setBuyNowPrice(150.);
		Product product = new HarvestProduct();
		product.setId(1);

		auction.setOptions(options);
		auction.setProduct(product);

		AuctionDto auctionDto = auctionMapper.toDto(auction);

		assertNotNull(auctionDto);
		assertEquals(auction.getId(), auctionDto.getId());
		assertEquals(auction.getPrice(), auctionDto.getPrice());
		assertEquals(auction.getProductQuantity(), auctionDto.getProductQuantity());
		assertEquals(auction.getExpirationDate(), auctionDto.getExpirationDate());
		assertEquals(auction.getCreationDate(), auctionDto.getCreationDate());
		assertEquals(auction.getActive(), auctionDto.getActive());
		assertEquals(auction.getOptions().getStrategy().getId(),
				auctionDto.getOptions().getStrategy().getId());
		assertEquals(auction.getProduct().getId(), auctionDto.getProduct().getId());
		assertEquals(auction.getTrader().getId(), auctionDto.getTrader().getId());
		assertEquals(auction.getStatus().getId(), auctionDto.getStatus().getId());
	}

	@Test
	public void testToEntity() {
		AuctionOptionsUpdateDto optionsDto = new AuctionOptionsUpdateDto();
		AuctionStrategyDto strategyDto = new AuctionStrategyDto();
		strategyDto.setName("BestOffer");
		optionsDto.setStrategyId(1);
		optionsDto.setBuyNowPrice(100.50);
		optionsDto.setShowPublic(true);
		AuctionUpdateDto auctionDto = new AuctionUpdateDto();
		auctionDto.setPrice(100.50);
		auctionDto.setProductQuantity(10);
		auctionDto.setExpirationDate(LocalDateTime.of(2025, 12, 31, 23, 59, 59, 0));
		auctionDto.setCreationDate(LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0));
		auctionDto.setTraderId(1);
		auctionDto.setStatusId(2);
		auctionDto.setActive(true);
		auctionDto.setOptions(optionsDto);
		auctionDto.setProductId(4);

		Auction auction = auctionMapper.toEntity(auctionDto);

		assertNotNull(auction);
		assertEquals(auctionDto.getPrice(), auction.getPrice());
		assertEquals(auctionDto.getProductQuantity(), auction.getProductQuantity());
		assertEquals(auctionDto.getExpirationDate(), auction.getExpirationDate());
		assertEquals(auctionDto.getCreationDate(), auction.getCreationDate());
		assertEquals(auctionDto.getActive(), auction.getActive());
		assertEquals(auctionDto.getOptions().getStrategyId(),
				auction.getOptions().getStrategy().getId());
		assertEquals(auctionDto.getProductId(), auction.getProduct().getId());
		assertEquals(auctionDto.getTraderId(), auction.getTrader().getId());
		assertEquals(auctionDto.getStatusId(), auction.getStatus().getId());
	}

	@Test
	public void testUpdateToEntity() {
		AuctionUpdateDto auctionDto = getAuctionUpdateDto();
		Auction existingAuction = new Auction();
		existingAuction.setProductQuantity(20);
		existingAuction.setPrice(50.);

		existingAuction.setPrice(90.);
		existingAuction.setExpirationDate(LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0));
		existingAuction.setCreationDate(LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0));
		existingAuction.setActive(true);
		existingAuction.setOptions();
		existingAuction.setProductQuantity(20);
		existingAuction.setTrader(new Producer());
		existingAuction.setStatus(new TradeStatus());

		Auction auction = auctionMapper.partialUpdate(auctionDto, existingAuction);

		assertNotNull(auction);
		assertEquals(auctionDto.getPrice(), auction.getPrice());
		assertEquals(auctionDto.getProductQuantity(), auction.getProductQuantity());
		assertEquals(auctionDto.getExpirationDate(), auction.getExpirationDate());
		assertEquals(auctionDto.getCreationDate(), auction.getCreationDate());
		assertEquals(auctionDto.getActive(), auction.getActive());
		assertEquals(auctionDto.getOptions().getStrategyId(),
				auction.getOptions().getStrategy().getId());
		assertEquals(auctionDto.getProductId(), auction.getProduct().getId());
		assertEquals(auctionDto.getTraderId(), auction.getTrader().getId());
		assertEquals(auctionDto.getStatusId(), auction.getStatus().getId());
	}

	private static AuctionUpdateDto getAuctionUpdateDto() {
		AuctionOptionsUpdateDto optionsDto = new AuctionOptionsUpdateDto();
		AuctionStrategyDto strategyDto = new AuctionStrategyDto();
		strategyDto.setName("BestOffer");
		optionsDto.setStrategyId(1);
		optionsDto.setBuyNowPrice(100.50);
		optionsDto.setShowPublic(true);
		AuctionUpdateDto auctionDto = new AuctionUpdateDto();
		auctionDto.setPrice(100.50);
		auctionDto.setProductQuantity(10);
		auctionDto.setExpirationDate(LocalDateTime.of(2025, 12, 31, 23, 59, 59, 0));
		auctionDto.setCreationDate(LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0));
		auctionDto.setTraderId(1);
		auctionDto.setStatusId(2);
		auctionDto.setOptions(optionsDto);
		auctionDto.setProductId(4);
		auctionDto.setActive(true);
		return auctionDto;
	}

	private static AuctionUpdateDto getAuctionOptions() {
		AuctionOptionsUpdateDto optionsDto = new AuctionOptionsUpdateDto();
		AuctionStrategyDto strategyDto = new AuctionStrategyDto();
		strategyDto.setName("BestOffer");
		optionsDto.setStrategyId(1);
		optionsDto.setBuyNowPrice(100.50);
		optionsDto.setShowPublic(true);
		AuctionUpdateDto auctionDto = new AuctionUpdateDto();
		auctionDto.setPrice(100.50);
		auctionDto.setProductQuantity(10);
		auctionDto.setExpirationDate(LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0));
		auctionDto.setCreationDate(LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0));
		auctionDto.setTraderId(1);
		auctionDto.setStatusId(2);
		auctionDto.setOptions(optionsDto);
		auctionDto.setProductId(4);
		auctionDto.setActive(true);
		return auctionDto;
	}
}
