package be.labil.anacarde.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import be.labil.anacarde.domain.dto.AuctionDto;
import be.labil.anacarde.domain.dto.AuctionStrategyDto;
import be.labil.anacarde.domain.dto.HarvestProductDto;
import be.labil.anacarde.domain.dto.ProductDto;
import be.labil.anacarde.domain.dto.user.TransformerDetailDto;
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
	}

	@Test
	public void testToEntity() {
		AuctionDto auctionDto = new AuctionDto();
		auctionDto.setId(1);
		auctionDto.setPrice(BigDecimal.valueOf(100.50));
		auctionDto.setProductQuantity(10);
		auctionDto.setExpirationDate(LocalDateTime.of(2025, 12, 31, 23, 59, 59, 0));
		auctionDto.setCreationDate(LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0));
		auctionDto.setTrader(new TransformerDetailDto());
		auctionDto.setActive(true);

		AuctionStrategyDto strategyDto = new AuctionStrategyDto();
		strategyDto.setId(1);
		ProductDto productDto = new HarvestProductDto();
		strategyDto.setId(2);

		auctionDto.setStrategy(strategyDto);
		auctionDto.setProduct(productDto);

		Auction auction = auctionMapper.toEntity(auctionDto);

		assertNotNull(auction);
		assertEquals(auctionDto.getId(), auction.getId());
		assertEquals(auctionDto.getPrice(), auction.getPrice());
		assertEquals(auctionDto.getProductQuantity(), auction.getProductQuantity());
		assertEquals(auctionDto.getExpirationDate(), auction.getExpirationDate());
		assertEquals(auctionDto.getCreationDate(), auction.getCreationDate());
		assertEquals(auctionDto.getActive(), auction.getActive());
		assertEquals(auctionDto.getStrategy().getId(), auction.getStrategy().getId());
		assertEquals(auctionDto.getProduct().getId(), auction.getProduct().getId());
		assertEquals(auctionDto.getTrader().getId(), auction.getTrader().getId());
	}
}
