package be.labil.anacarde.domain.mapper;

import static org.junit.jupiter.api.Assertions.*;

import be.labil.anacarde.domain.dto.*;
import be.labil.anacarde.domain.dto.user.TransformerDetailDto;
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
		bid.setAuctionDate(LocalDateTime.of(2025, 4, 7, 12, 0));
		bid.setCreationDate(LocalDateTime.of(2025, 4, 7, 11, 30));
		Auction auction = new Auction();
		auction.setId(1);
		auction.setProduct(HarvestProduct.builder().id(1).build());
		bid.setAuction(auction);
		bid.setTrader(new Transformer());
		bid.setStatus(new BidStatus());

		BidDto dto = bidMapper.toDto(bid);

		assertNotNull(dto);
		assertEquals(bid.getId(), dto.getId());
		assertEquals(bid.getAmount(), dto.getAmount());
		assertEquals(bid.getAuctionDate(), dto.getAuctionDate());
		assertEquals(bid.getCreationDate(), dto.getCreationDate());
		assertNotNull(dto.getAuction());
		assertNotNull(dto.getTrader());
		assertNotNull(dto.getStatus());
	}

	@Test
	void testToEntity() {
		BidDto dto = new BidDto();
		dto.setId(2);
		dto.setAmount(new BigDecimal("180.50"));
		dto.setAuctionDate(LocalDateTime.of(2025, 4, 8, 14, 0));
		dto.setCreationDate(LocalDateTime.of(2025, 4, 8, 13, 0));
		AuctionDto auction = new AuctionDto();
		auction.setId(1);
		auction.setProduct(new HarvestProductDto());
		dto.setAuction(auction);
		dto.setTrader(new TransformerDetailDto());
		dto.setStatus(new BidStatusDto());

		Bid entity = bidMapper.toEntity(dto);

		assertNotNull(entity);
		assertEquals(dto.getId(), entity.getId());
		assertEquals(dto.getAmount(), entity.getAmount());
		assertEquals(dto.getAuctionDate(), entity.getAuctionDate());
		assertEquals(dto.getCreationDate(), entity.getCreationDate());
		assertNotNull(entity.getAuction());
		assertNotNull(entity.getTrader());
		assertNotNull(entity.getStatus());
	}

	@Test
	void testPartialUpdate() {
		BidDto dto = new BidDto();
		dto.setAmount(new BigDecimal("300.00"));

		Bid existing = new Bid();
		existing.setId(10);
		existing.setAmount(new BigDecimal("100.00"));
		existing.setAuctionDate(LocalDateTime.of(2025, 1, 1, 10, 0));
		existing.setCreationDate(LocalDateTime.of(2025, 1, 1, 9, 0));
		existing.setAuction(new Auction());
		existing.setTrader(new Transformer());
		existing.setStatus(new BidStatus());

		Bid updated = bidMapper.partialUpdate(dto, existing);

		assertEquals(10, updated.getId());
		assertEquals(new BigDecimal("300.00"), updated.getAmount());
		assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), updated.getAuctionDate());
		assertNotNull(updated.getAuction());
		assertNotNull(updated.getTrader());
		assertNotNull(updated.getStatus());
	}
}
