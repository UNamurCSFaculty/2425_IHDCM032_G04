package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.AdminDto;
import be.labil.anacarde.domain.model.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring")
public interface AdminMapper extends GenericMapper<AdminDto, Admin> {

	@Override
	Admin toEntity(AdminDto dto);

	@Override
	AdminDto toDto(Admin admin);

	@ObjectFactory
	default Admin createAdmin(AdminDto dto) {
		return new Admin();
	}
}
