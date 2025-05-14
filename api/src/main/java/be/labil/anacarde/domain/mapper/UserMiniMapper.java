package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.user.*;
import be.labil.anacarde.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = MapperHelpers.class)
public abstract class UserMiniMapper {

	public abstract UserMiniDto toDto(Trader user);
	public abstract UserMiniDto toDto(User user);
}
