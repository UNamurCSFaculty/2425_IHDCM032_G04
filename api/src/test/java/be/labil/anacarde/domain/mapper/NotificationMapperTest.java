package be.labil.anacarde.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import be.labil.anacarde.domain.dto.db.NotificationDto;
import be.labil.anacarde.domain.model.Carrier;
import be.labil.anacarde.domain.model.Notification;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NotificationMapperTest {

	@Autowired
	private NotificationMapper notificationMapper;

	@Test
	void shouldMapEntityToDto() {
		Carrier user = new Carrier();
		user.setId(42);

		Notification notification = new Notification();
		notification.setId(1);
		notification.setMessage("Votre commande a été expédiée");
		notification.setType("INFO");
		notification.setSendDate(LocalDateTime.of(2025, 4, 1, 10, 30));
		notification.setReadStatus("UNREAD");
		notification.setUser(user);

		NotificationDto dto = notificationMapper.toDto(notification);

		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(notification.getId());
		assertThat(dto.getMessage()).isEqualTo(notification.getMessage());
		assertThat(dto.getType()).isEqualTo(notification.getType());
		assertThat(dto.getSendDate()).isEqualTo(notification.getSendDate());
		assertThat(dto.getReadStatus()).isEqualTo(notification.getReadStatus());
		assertThat(dto.getUserId()).isEqualTo(user.getId());
	}

	@Test
	void shouldMapDtoToEntity() {
		NotificationDto dto = new NotificationDto();
		dto.setId(1);
		dto.setMessage("Votre commande a été expédiée");
		dto.setType("INFO");
		dto.setSendDate(LocalDateTime.of(2025, 4, 1, 10, 30));
		dto.setReadStatus("UNREAD");
		dto.setUserId(42);

		Notification entity = notificationMapper.toEntity(dto);

		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isEqualTo(dto.getId());
		assertThat(entity.getMessage()).isEqualTo(dto.getMessage());
		assertThat(entity.getType()).isEqualTo(dto.getType());
		assertThat(entity.getSendDate()).isEqualTo(dto.getSendDate());
		assertThat(entity.getReadStatus()).isEqualTo(dto.getReadStatus());
		assertThat(entity.getUser()).isNotNull();
		assertThat(entity.getUser().getId()).isEqualTo(dto.getUserId());
	}
}
