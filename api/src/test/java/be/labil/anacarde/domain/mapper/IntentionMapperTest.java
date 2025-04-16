package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import be.labil.anacarde.domain.dto.IntentionDto;
import be.labil.anacarde.domain.dto.QualityDto;
import be.labil.anacarde.domain.dto.user.TraderDetailDto;
import be.labil.anacarde.domain.dto.user.TransformerDetailDto;
import be.labil.anacarde.domain.model.Intention;
import be.labil.anacarde.domain.model.Quality;
import be.labil.anacarde.domain.model.Trader;
import be.labil.anacarde.domain.model.Transformer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class IntentionMapperTest {

	@Autowired
	private IntentionMapper mapper;

	@Test
	void shouldMapEntityToDto() {
		Quality quality = new Quality();
		quality.setId(1);

		Trader buyer = new Transformer();
		buyer.setId(2);

		Intention entity = new Intention();
		entity.setId(100);
		entity.setPrice(new BigDecimal("300.50"));
		entity.setDate(LocalDateTime.of(2025, 4, 7, 10, 30));
		entity.setQuantity(1000);
		entity.setQuality(quality);
		entity.setBuyer(buyer);

		IntentionDto dto = mapper.toDto(entity);

		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(100);
		assertThat(dto.getPrice()).isEqualTo(new BigDecimal("300.50"));
		assertThat(dto.getDate()).isEqualTo(LocalDateTime.of(2025, 4, 7, 10, 30));
		assertThat(dto.getQuantity()).isEqualTo(1000);
		assertThat(dto.getQuality()).isNotNull();
		assertThat(dto.getQuality().getId()).isEqualTo(1);
		assertThat(dto.getBuyer()).isNotNull();
		assertThat(dto.getBuyer().getId()).isEqualTo(2);
	}

	@Test
	void shouldMapDtoToEntity() {
		QualityDto qualityDto = new QualityDto();
		qualityDto.setId(1);

		TraderDetailDto buyerDto = new TransformerDetailDto();
		buyerDto.setId(2);

		IntentionDto dto = new IntentionDto();
		dto.setId(100);
		dto.setPrice(new BigDecimal("400.75"));
		dto.setDate(LocalDateTime.of(2025, 4, 8, 12, 0));
		dto.setQuantity(1500);
		dto.setQuality(qualityDto);
		dto.setBuyer(buyerDto);

		Intention entity = mapper.toEntity(dto);

		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isEqualTo(100);
		assertThat(entity.getPrice()).isEqualTo(new BigDecimal("400.75"));
		assertThat(entity.getDate()).isEqualTo(LocalDateTime.of(2025, 4, 8, 12, 0));
		assertThat(entity.getQuantity()).isEqualTo(1500);
		assertThat(entity.getQuality()).isNotNull();
		assertThat(entity.getQuality().getId()).isEqualTo(1);
		assertThat(entity.getBuyer()).isNotNull();
		assertThat(entity.getBuyer().getId()).isEqualTo(2);
	}

	@Test
	void shouldPartiallyUpdateEntity() {
		Quality oldQuality = new Quality();
		oldQuality.setId(1);

		Trader oldBuyer = new Transformer();
		oldBuyer.setId(2);

		Intention entity = new Intention();
		entity.setId(200);
		entity.setPrice(new BigDecimal("100.00"));
		entity.setDate(LocalDateTime.of(2025, 1, 1, 10, 0));
		entity.setQuantity(500);
		entity.setQuality(oldQuality);
		entity.setBuyer(oldBuyer);

		QualityDto newQualityDto = new QualityDto();
		newQualityDto.setId(3);

		TraderDetailDto newBuyerDto = new TransformerDetailDto();
		newBuyerDto.setId(4);

		IntentionDto updateDto = new IntentionDto();
		updateDto.setPrice(new BigDecimal("120.00"));
		updateDto.setQuantity(600);
		updateDto.setQuality(newQualityDto);
		updateDto.setBuyer(newBuyerDto);

		mapper.partialUpdate(updateDto, entity);

		assertThat(entity.getPrice()).isEqualTo(new BigDecimal("120.00"));
		assertThat(entity.getQuantity()).isEqualTo(600);
		assertThat(entity.getQuality().getId()).isEqualTo(3);
		assertThat(entity.getBuyer().getId()).isEqualTo(4);
		assertThat(entity.getDate()).isEqualTo(LocalDateTime.of(2025, 1, 1, 10, 0)); // unchanged
	}
}
