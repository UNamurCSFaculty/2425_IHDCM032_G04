package be.labil.anacarde.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import be.labil.anacarde.domain.dto.db.AuctionDto;
import be.labil.anacarde.domain.dto.write.AuctionUpdateDto;
import be.labil.anacarde.domain.model.*;
import java.math.BigDecimal;
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
		auction.setPrice(BigDecimal.valueOf(100.50));
		auction.setProductQuantity(10);
		auction.setExpirationDate(LocalDateTime.of(2025, 12, 31, 23, 59, 59, 0));
		auction.setCreationDate(LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0));
		auction.setTrader(new Transformer());
		auction.setStatus(new TradeStatus());
		auction.setActive(true);

		AuctionStrategy strategy = new AuctionStrategy();
		strategy.setId(1);
		Product product = new HarvestProduct();
		product.setId(1);

		auction.setStrategy(strategy);
		auction.setProduct(product);

		AuctionDto auctionDto = auctionMapper.toDto(auction);

		assertNotNull(auctionDto);
		assertEquals(auction.getId(), auctionDto.getId());
		assertEquals(auction.getPrice(), auctionDto.getPrice());
		assertEquals(auction.getProductQuantity(), auctionDto.getProductQuantity());
		assertEquals(auction.getExpirationDate(), auctionDto.getExpirationDate());
		assertEquals(auction.getCreationDate(), auctionDto.getCreationDate());
		assertEquals(auction.getActive(), auctionDto.getActive());
		assertEquals(auction.getStrategy().getId(), auctionDto.getStrategy().getId());
		assertEquals(auction.getProduct().getId(), auctionDto.getProduct().getId());
		assertEquals(auction.getTrader().getId(), auctionDto.getTrader().getId());
		assertEquals(auction.getStatus().getId(), auctionDto.getStatus().getId());
	}

	@Test
	public void testToEntity() {
		AuctionUpdateDto auctionDto = new AuctionUpdateDto();
		auctionDto.setPrice(BigDecimal.valueOf(100.50));
		auctionDto.setProductQuantity(10);
		auctionDto.setExpirationDate(LocalDateTime.of(2025, 12, 31, 23, 59, 59, 0));
		auctionDto.setCreationDate(LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0));
		auctionDto.setTraderId(1);
		auctionDto.setStatusId(2);
		auctionDto.setActive(true);
		auctionDto.setStrategyId(3);
		auctionDto.setProductId(4);

		Auction auction = auctionMapper.toEntity(auctionDto);

		assertNotNull(auction);
		assertEquals(auctionDto.getPrice(), auction.getPrice());
		assertEquals(auctionDto.getProductQuantity(), auction.getProductQuantity());
		assertEquals(auctionDto.getExpirationDate(), auction.getExpirationDate());
		assertEquals(auctionDto.getCreationDate(), auction.getCreationDate());
		assertEquals(auctionDto.getActive(), auction.getActive());
		assertEquals(auctionDto.getStrategyId(), auction.getStrategy().getId());
		assertEquals(auctionDto.getProductId(), auction.getProduct().getId());
		assertEquals(auctionDto.getTraderId(), auction.getTrader().getId());
		assertEquals(auctionDto.getStatusId(), auction.getStatus().getId());
	}

	@Test
	public void testUpdateToEntity() {
		AuctionUpdateDto auctionDto = new AuctionUpdateDto();
		auctionDto.setPrice(BigDecimal.valueOf(100.50));
		auctionDto.setProductQuantity(10);
		auctionDto.setExpirationDate(LocalDateTime.of(2025, 12, 31, 23, 59, 59, 0));
		auctionDto.setCreationDate(LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0));
		auctionDto.setTraderId(1);
		auctionDto.setStatusId(2);
		auctionDto.setStrategyId(3);
		auctionDto.setProductId(4);
		auctionDto.setActive(true);

		Auction existingAuction = new Auction();
		existingAuction.setProductQuantity(20);
		existingAuction.setPrice(BigDecimal.valueOf(50));

		Auction auction = auctionMapper.partialUpdate(auctionDto, existingAuction);

		assertNotNull(auction);
		assertEquals(auctionDto.getPrice(), auction.getPrice());
		assertEquals(auctionDto.getProductQuantity(), auction.getProductQuantity());
		assertEquals(auctionDto.getExpirationDate(), auction.getExpirationDate());
		assertEquals(auctionDto.getCreationDate(), auction.getCreationDate());
		assertEquals(auctionDto.getActive(), auction.getActive());
		assertEquals(auctionDto.getStrategyId(), auction.getStrategy().getId());
		assertEquals(auctionDto.getProductId(), auction.getProduct().getId());
		assertEquals(auctionDto.getTraderId(), auction.getTrader().getId());
		assertEquals(auctionDto.getStatusId(), auction.getStatus().getId());
	}
}
