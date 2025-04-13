package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.user.*;
import be.labil.anacarde.domain.model.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {RoleMapper.class,
		LanguageMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserDetailMapper extends GenericMapper<UserDetailDto, User> {

	@Override
	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	User toEntity(UserDetailDto dto);

	@Override
	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	UserDetailDto toDto(User user);

	@Override
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	// alternative @Mapping(target = "id", ignore = true) // On ignore l'id pour la mise à jour partielle
	@Mapping(target = "roles", ignore = true) // On ignore les rôles pour la mise à jour partielle
	User partialUpdate(UserDetailDto dto, @MappingTarget User entity);

	@ObjectFactory
	default User createUser(UserDetailDto dto) {
		if (dto instanceof AdminDetailDto) {
			return new Admin();
		} else if (dto instanceof ExporterDetailDto) {
			return new Exporter();
		} else if (dto instanceof CarrierDetailDto) {
			return new Carrier();
		} else if (dto instanceof QualityInspectorDetailDto) {
			return new QualityInspector();
		} else if (dto instanceof ProducerDetailDto) {
			return new Producer();
		} else if (dto instanceof TransformerDetailDto) {
			return new Transformer();
		}
		throw new IllegalArgumentException("Type de UserDetailDto non supporté : " + dto.getClass());
	}

	@ObjectFactory
	default UserDetailDto createUserDto(User user) {
		if (user instanceof Admin) {
			return new AdminDetailDto();
		} else if (user instanceof Exporter) {
			return new ExporterDetailDto();
		} else if (user instanceof Carrier) {
			return new CarrierDetailDto();
		} else if (user instanceof QualityInspector) {
			return new QualityInspectorDetailDto();
		} else if (user instanceof Producer) {
			return new ProducerDetailDto();
		} else if (user instanceof Transformer) {
			return new TransformerDetailDto();
		}

		throw new IllegalArgumentException("Type de User non supporté : " + user.getClass());
	}
}