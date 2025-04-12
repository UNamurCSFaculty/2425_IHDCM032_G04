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
		Intention intention = new Intention(1, BigDecimal.valueOf(2000), LocalDateTime.now(), 100, quality, trader);

		Interest interest = new Interest(10, BigDecimal.valueOf(1500), LocalDateTime.now(), intention, trader);

		InterestDto dto = interestMapper.toDto(interest);

		assertNotNull(dto);
		assertEquals(interest.getId(), dto.getId());
		assertEquals(interest.getPrice(), dto.getPrice());
		assertEquals(interest.getIntention().getId(), dto.getIntention().getId());
		assertEquals(interest.getBuyer().getId(), dto.getBuyer().getId());
	}

	@Test
	void testToEntity() {
		TraderDetailDto traderDto = new TransformerDetailDto();
		traderDto.setId(1);
		traderDto.setFirstName("John Doe");
		QualityDto qualityDto = new QualityDto();
		qualityDto.setId(1);
		qualityDto.setName("A");
		IntentionDto intentionDto = new IntentionDto();
		intentionDto.setId(2);
		intentionDto.setPrice(BigDecimal.valueOf(3000));
		intentionDto.setDate(LocalDateTime.now());
		intentionDto.setQuantity(500);
		intentionDto.setQuality(qualityDto);
		intentionDto.setBuyer(traderDto);

		InterestDto dto = new InterestDto();
		dto.setId(5);
		dto.setPrice(BigDecimal.valueOf(1600));
		dto.setDate(LocalDateTime.now());
		dto.setIntention(intentionDto);
		dto.setBuyer(traderDto);

		Interest entity = interestMapper.toEntity(dto);

		assertNotNull(entity);
		assertEquals(dto.getId(), entity.getId());
		assertEquals(dto.getPrice(), entity.getPrice());
		assertEquals(dto.getIntention().getId(), entity.getIntention().getId());
		assertEquals(dto.getBuyer().getId(), entity.getBuyer().getId());
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
		Intention intention = new Intention(20, BigDecimal.valueOf(2200), LocalDateTime.now(), 300, quality, trader);
		entity.setIntention(intention);
		entity.setBuyer(trader);

		InterestDto dto = new InterestDto();
		dto.setPrice(BigDecimal.valueOf(2500)); // Updating only price

		interestMapper.partialUpdate(dto, entity);

		assertEquals(BigDecimal.valueOf(2500), entity.getPrice());
		assertEquals(20, entity.getIntention().getId()); // unchanged
		assertEquals(5, entity.getBuyer().getId()); // unchanged
	}
}
