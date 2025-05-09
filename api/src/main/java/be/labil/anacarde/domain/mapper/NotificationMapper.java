package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.NotificationDto;
import be.labil.anacarde.domain.model.Carrier;
import be.labil.anacarde.domain.model.Notification;
import be.labil.anacarde.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class NotificationMapper {

	@Mapping(source = "user.id", target = "userId")
	public abstract NotificationDto toDto(Notification notification);

	@Mapping(source = "userId", target = "user", qualifiedByName = "mapUserIdToUser")
	public abstract Notification toEntity(NotificationDto dto);

	@Named("mapUserIdToUser")
	public static User mapUserIdToUser(Integer userId) {
		if (userId == null) {
			return null;
		}
		// Par défaut, on retourne un Carrier comme implémentation de User.
		Carrier user = new Carrier();
		user.setId(userId);
		return user;
	}
}
