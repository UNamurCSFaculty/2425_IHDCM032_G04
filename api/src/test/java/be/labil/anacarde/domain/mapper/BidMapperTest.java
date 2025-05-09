package be.labil.anacarde.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import be.labil.anacarde.domain.dto.db.AuctionDto;
import be.labil.anacarde.domain.dto.db.BidDto;
import be.labil.anacarde.domain.dto.db.product.HarvestProductDto;
import be.labil.anacarde.domain.dto.write.BidUpdateDto;
import be.labil.anacarde.domain.model.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BidMapperTest {

	@Autowired
	private BidMapper bidMapper;

	@Test
	void testToDto() {
		Bid bid = new Bid();
		bid.setId(1);
		bid.setAmount(new BigDecimal("250.00"));
		bid.setCreationDate(LocalDateTime.of(2025, 4, 7, 11, 30));
		Auction auction = new Auction();
		auction.setId(1);
		auction.setProduct(HarvestProduct.builder().id(1).build());
		bid.setAuctionId(auction.getId());
		bid.setTrader(new Transformer());
		bid.setStatus(new TradeStatus());

		BidDto dto = bidMapper.toDto(bid);

		assertNotNull(dto);
		assertEquals(bid.getId(), dto.getId());
		assertEquals(bid.getAmount(), dto.getAmount());
		assertEquals(bid.getCreationDate(), dto.getCreationDate());
		assertNotNull(dto.getAuctionId());
		assertNotNull(dto.getTrader());
		assertNotNull(dto.getStatus());
	}

	@Test
	void testToEntity() {
		BidUpdateDto dto = new BidUpdateDto();
		dto.setAmount(new BigDecimal("180.50"));
		dto.setCreationDate(LocalDateTime.of(2025, 4, 8, 13, 0));
		AuctionDto auction = new AuctionDto();
		auction.setId(1);
		auction.setProduct(new HarvestProductDto());
		dto.setAuctionId(auction.getId());
		dto.setTraderId(1);
		dto.setStatusId(2);

		Bid entity = bidMapper.toEntity(dto);

		assertNotNull(entity);
		assertEquals(dto.getAmount(), entity.getAmount());
		assertEquals(dto.getCreationDate(), entity.getCreationDate());
		assertNotNull(entity.getAuctionId());
		assertNotNull(entity.getTrader());
		assertNotNull(entity.getStatus());
	}

	@Test
	void testUpdateToEntity() {
		BidUpdateDto dto = new BidUpdateDto();
		dto.setAmount(new BigDecimal("180.50"));
		dto.setCreationDate(LocalDateTime.of(2025, 4, 8, 13, 0));
		AuctionDto auction = new AuctionDto();
		auction.setId(1);
		auction.setProduct(new HarvestProductDto());
		dto.setAuctionId(auction.getId());
		dto.setTraderId(2);
		dto.setStatusId(2);

		Bid entity = bidMapper.toEntity(dto);

		assertNotNull(entity);
		assertEquals(dto.getAmount(), entity.getAmount());
		assertEquals(dto.getCreationDate(), entity.getCreationDate());
		assertNotNull(entity.getAuctionId());
		assertNotNull(entity.getTrader());
		assertNotNull(entity.getStatus());
	}

	@Test
	void testPartialUpdate() {
		BidUpdateDto dto = new BidUpdateDto();
		dto.setAmount(new BigDecimal("300.00"));

		Bid existing = new Bid();
		existing.setId(10);
		existing.setAmount(new BigDecimal("100.00"));
		existing.setCreationDate(LocalDateTime.of(2025, 1, 1, 9, 0));
		existing.setAuctionId(1);
		existing.setTrader(new Transformer());
		existing.setStatus(new TradeStatus());

		Bid updated = bidMapper.partialUpdate(dto, existing);

		assertEquals(10, updated.getId());
		assertEquals(new BigDecimal("300.00"), updated.getAmount());
		assertEquals(LocalDateTime.of(2025, 1, 1, 9, 0), updated.getCreationDate());
		assertEquals(1, updated.getAuctionId());
		assertNotNull(updated.getTrader());
		assertNotNull(updated.getStatus());
	}
}
