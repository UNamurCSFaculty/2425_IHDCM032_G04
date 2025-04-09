package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.NotificationDto;
import be.labil.anacarde.domain.model.Carrier;
import be.labil.anacarde.domain.model.Notification;
import be.labil.anacarde.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper extends GenericMapper<NotificationDto, Notification> {

	@Override
	@Mapping(source = "user.id", target = "userId")
	NotificationDto toDto(Notification notification);

	@Override
	@Mapping(source = "userId", target = "user", qualifiedByName = "mapUserIdToUser")
	Notification toEntity(NotificationDto dto);

	@Named("mapUserIdToUser")
	default User mapUserIdToUser(Integer userId) {
		if (userId == null) {
			return null;
		}
		// Par défaut, on retourne un Carrier comme implémentation de User.
		Carrier user = new Carrier();
		user.setId(userId);
		return user;
	}
}
