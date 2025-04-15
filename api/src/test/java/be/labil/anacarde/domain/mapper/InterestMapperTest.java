package be.labil.anacarde.domain.mapper;

import static org.junit.jupiter.api.Assertions.*;

import be.labil.anacarde.domain.dto.IntentionDto;
import be.labil.anacarde.domain.dto.InterestDto;
import be.labil.anacarde.domain.dto.QualityDto;
import be.labil.anacarde.domain.dto.user.TraderDetailDto;
import be.labil.anacarde.domain.dto.user.TransformerDetailDto;
import be.labil.anacarde.domain.model.Intention;
import be.labil.anacarde.domain.model.Interest;
import be.labil.anacarde.domain.model.Quality;
import be.labil.anacarde.domain.model.Trader;
import be.labil.anacarde.domain.model.Transformer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InterestMapperTest {

	@Autowired
	private InterestMapper interestMapper;

	@Test
	void testToDto() {
		Trader trader = new Transformer();
		trader.setId(1);
		trader.setFirstName("John Doe");

		Quality quality = new Quality(1, "Premium");

		LocalDateTime intentionDate = LocalDateTime.of(2024, 4, 10, 12, 0);
		Intention intention = new Intention(1, BigDecimal.valueOf(2000), intentionDate, 100, quality, trader);

		LocalDateTime interestDate = LocalDateTime.of(2024, 4, 11, 14, 0);
		Interest interest = new Interest(10, BigDecimal.valueOf(1500), interestDate, intention, trader);

		InterestDto dto = interestMapper.toDto(interest);

		assertNotNull(dto);
		assertEquals(10, dto.getId());
		assertEquals(BigDecimal.valueOf(1500), dto.getPrice());
		assertEquals(interestDate, dto.getDate());

		assertNotNull(dto.getIntention());
		assertEquals(1, dto.getIntention().getId());
		assertEquals(BigDecimal.valueOf(2000), dto.getIntention().getPrice());
		assertEquals(intentionDate, dto.getIntention().getDate());
		assertEquals(100, dto.getIntention().getQuantity());
		assertEquals(1, dto.getIntention().getQuality().getId());
		assertEquals("Premium", dto.getIntention().getQuality().getName());

		assertNotNull(dto.getBuyer());
		assertEquals(1, dto.getBuyer().getId());
		assertEquals("John Doe", dto.getBuyer().getFirstName());
	}

	@Test
	void testToEntity() {
		TraderDetailDto traderDto = new TransformerDetailDto();
		traderDto.setId(1);
		traderDto.setFirstName("John Doe");

		QualityDto qualityDto = new QualityDto();
		qualityDto.setId(1);
		qualityDto.setName("A");

		LocalDateTime intentionDate = LocalDateTime.of(2024, 4, 10, 16, 0);
		IntentionDto intentionDto = new IntentionDto();
		intentionDto.setId(2);
		intentionDto.setPrice(BigDecimal.valueOf(3000));
		intentionDto.setDate(intentionDate);
		intentionDto.setQuantity(500);
		intentionDto.setQuality(qualityDto);
		intentionDto.setBuyer(traderDto);

		LocalDateTime interestDate = LocalDateTime.of(2024, 4, 11, 9, 0);
		InterestDto dto = new InterestDto();
		dto.setId(5);
		dto.setPrice(BigDecimal.valueOf(1600));
		dto.setDate(interestDate);
		dto.setIntention(intentionDto);
		dto.setBuyer(traderDto);

		Interest entity = interestMapper.toEntity(dto);

		assertNotNull(entity);
		assertEquals(5, entity.getId());
		assertEquals(BigDecimal.valueOf(1600), entity.getPrice());
		assertEquals(interestDate, entity.getDate());

		assertNotNull(entity.getIntention());
		assertEquals(2, entity.getIntention().getId());
		assertEquals(BigDecimal.valueOf(3000), entity.getIntention().getPrice());
		assertEquals(intentionDate, entity.getIntention().getDate());
		assertEquals(500, entity.getIntention().getQuantity());
		assertEquals(1, entity.getIntention().getQuality().getId());
		assertEquals("A", entity.getIntention().getQuality().getName());

		assertNotNull(entity.getBuyer());
		assertEquals(1, entity.getBuyer().getId());
		assertEquals("John Doe", entity.getBuyer().getFirstName());
	}

	@Test
	void testPartialUpdate() {
		Interest entity = new Interest();
		entity.setId(99);
		entity.setPrice(BigDecimal.valueOf(2000));
		entity.setDate(LocalDateTime.of(2024, 1, 1, 10, 0));

		Trader trader = new Transformer();
		trader.setId(5);
		trader.setFirstName("Jane Smith");

		Quality quality = new Quality(6, "Standard");

		LocalDateTime intentionDate = LocalDateTime.of(2023, 12, 31, 8, 0);
		Intention intention = new Intention(20, BigDecimal.valueOf(2200), intentionDate, 300, quality, trader);
		entity.setIntention(intention);
		entity.setBuyer(trader);

		InterestDto dto = new InterestDto();
		dto.setPrice(BigDecimal.valueOf(2500));

		interestMapper.partialUpdate(dto, entity);

		assertEquals(99, entity.getId()); // unchanged
		assertEquals(BigDecimal.valueOf(2500), entity.getPrice()); // updated
		assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), entity.getDate()); // unchanged
		assertEquals(20, entity.getIntention().getId()); // unchanged
		assertEquals(BigDecimal.valueOf(2200), entity.getIntention().getPrice());
		assertEquals(intentionDate, entity.getIntention().getDate());
		assertEquals(300, entity.getIntention().getQuantity());
		assertEquals(6, entity.getIntention().getQuality().getId());
		assertEquals("Standard", entity.getIntention().getQuality().getName());

		assertEquals(5, entity.getBuyer().getId());
		assertEquals("Jane Smith", entity.getBuyer().getFirstName());
	}
}
