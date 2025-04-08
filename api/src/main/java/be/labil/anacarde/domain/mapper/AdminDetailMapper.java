package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.user.AdminDetailDto;
import be.labil.anacarde.domain.model.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {RoleMapper.class,
		LanguageMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdminDetailMapper extends GenericMapper<AdminDetailDto, Admin> {

	@Override
	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	Admin toEntity(AdminDetailDto dto);

	@Override
	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	AdminDetailDto toDto(Admin admin);
}
