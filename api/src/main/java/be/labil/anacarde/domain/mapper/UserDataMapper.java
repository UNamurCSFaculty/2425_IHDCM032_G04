package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.user.UserDataDto;
import be.labil.anacarde.domain.model.*;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserDataMapper {

	@Mapping(source = "id", target = "id")
	@Mapping(source = "registrationDate", target = "registrationDate")
	@Mapping(target = "type", expression = "java(determineType(user))")
	UserDataDto toDto(User user);

	List<UserDataDto> toDtoList(List<User> users);

	default String determineType(User user) {
		if (user instanceof Admin) return "admin";
		if (user instanceof Producer) return "producer";
		if (user instanceof Transformer) return "transformer";
		if (user instanceof QualityInspector) return "quality_inspector";
		if (user instanceof Exporter) return "exporter";
		if (user instanceof Carrier) return "carrier";
		throw new IllegalArgumentException("Type d'utilisateur non supporté : " + user.getClass().getSimpleName());
	}
}
