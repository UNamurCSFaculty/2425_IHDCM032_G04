package be.labil.anacarde.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import be.labil.anacarde.domain.dto.ContractOfferDto;
import be.labil.anacarde.domain.dto.QualityDto;
import be.labil.anacarde.domain.dto.user.ProducerDetailDto;
import be.labil.anacarde.domain.dto.user.TraderDetailDto;
import be.labil.anacarde.domain.dto.user.TransformerDetailDto;
import be.labil.anacarde.domain.model.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ContractOfferMapperTest {

	@Autowired
	private ContractOfferMapper contractOfferMapper;

	@Test
	void testToDto() {
		Trader seller = new Producer();
		seller.setId(1);
		seller.setFirstName("Alice");
		seller.setEmail("alice@market.com");

		Trader buyer = new Transformer();
		buyer.setId(1);
		buyer.setFirstName("Bob");
		buyer.setEmail("bob@market.com");

		Quality quality = Quality.builder().id(1).name("Qualité Premium").build();

		ContractOffer entity = new ContractOffer();
		entity.setId(10);
		entity.setStatus("En attente");
		entity.setPricePerKg(new BigDecimal("2.50"));
		entity.setCreationDate(LocalDateTime.of(2025, 4, 7, 10, 0));
		entity.setCreationDate(LocalDateTime.of(2025, 4, 7, 10, 0));
		entity.setSeller(seller);
		entity.setBuyer(buyer);
		entity.setQuality(quality);

		ContractOfferDto dto = contractOfferMapper.toDto(entity);

		assertNotNull(dto);
		assertEquals(entity.getId(), dto.getId());
		assertEquals(entity.getStatus(), dto.getStatus());
		assertEquals(entity.getPricePerKg(), dto.getPricePerKg());
		assertEquals(entity.getCreationDate(), dto.getCreationDate());
		assertEquals(entity.getEndDate(), dto.getEndDate());

		assertEquals(entity.getSeller().getId(), dto.getSeller().getId());
		assertEquals(entity.getBuyer().getId(), dto.getBuyer().getId());
		assertEquals(entity.getQuality().getId(), dto.getQuality().getId());
	}

	@Test
	void testToEntity() {
		TraderDetailDto seller = new ProducerDetailDto();
		seller.setId(1);
		seller.setFirstName("Alice");

		TraderDetailDto buyer = new TransformerDetailDto();
		buyer.setId(2);
		buyer.setFirstName("Bob");

		QualityDto quality = new QualityDto();
		quality.setId(1);
		quality.setName("A");

		ContractOfferDto dto = new ContractOfferDto();
		dto.setId(10);
		dto.setStatus("Accepté");
		dto.setPricePerKg(new BigDecimal("3.00"));
		dto.setCreationDate(LocalDateTime.of(2025, 4, 8, 15, 0));
		dto.setEndDate(LocalDateTime.of(2025, 4, 8, 15, 0));
		dto.setSeller(seller);
		dto.setBuyer(buyer);
		dto.setQuality(quality);

		ContractOffer entity = contractOfferMapper.toEntity(dto);

		assertNotNull(entity);
		assertEquals(dto.getId(), entity.getId());
		assertEquals(dto.getStatus(), entity.getStatus());
		assertEquals(dto.getPricePerKg(), entity.getPricePerKg());
		assertEquals(dto.getCreationDate(), entity.getCreationDate());
		assertEquals(dto.getEndDate(), entity.getEndDate());

		assertEquals(dto.getSeller().getId(), entity.getSeller().getId());
		assertEquals(dto.getBuyer().getId(), entity.getBuyer().getId());
		assertEquals(dto.getQuality().getId(), entity.getQuality().getId());
	}
}
