package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.*;
import be.labil.anacarde.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

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

	// Implémentation par défaut pour obtenir l'instance du TraderDetailMapper
	default TraderDetailMapper getTraderDetailMapper() {
		return org.mapstruct.factory.Mappers.getMapper(TraderDetailMapper.class);
	}

	@ObjectFactory
	default User createUser(UserDetailDto dto) {
		if (dto instanceof AdminDetailDto) {
			return new Admin();
		} else if (dto instanceof TraderDetailDto) {
			return getTraderDetailMapper().createTrader((TraderDetailDto) dto);
		} else if (dto instanceof CarrierDetailDto) {
			return new Carrier();
		} else if (dto instanceof QualityInspectorDetailDto) {
			return new QualityInspector();
		}
		throw new IllegalArgumentException("Type de UserDetailDto non supporté : " + dto.getClass());
	}

	@ObjectFactory
	default UserDetailDto createUserDto(User user) {
		if (user instanceof Admin) {
			return new AdminDetailDto();
		} else if (user instanceof Trader) {
			return getTraderDetailMapper().createTrader((Trader) user);
		} else if (user instanceof Carrier) {
			return new CarrierDetailDto();
		} else if (user instanceof QualityInspector) {
			return new QualityInspectorDetailDto();
		}
		throw new IllegalArgumentException("Type de User non supporté : " + user.getClass());
	}
}