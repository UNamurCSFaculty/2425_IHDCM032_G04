package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.*;
import be.labil.anacarde.application.service.storage.StorageService;
import be.labil.anacarde.domain.dto.db.AddressDto;
import be.labil.anacarde.domain.dto.db.FieldDto;
import be.labil.anacarde.domain.dto.db.user.UserDetailDto;
import be.labil.anacarde.domain.dto.db.user.UserListDto;
import be.labil.anacarde.domain.dto.write.user.create.ProducerCreateDto;
import be.labil.anacarde.domain.dto.write.user.create.UserCreateDto;
import be.labil.anacarde.domain.dto.write.user.update.ProducerUpdateDto;
import be.labil.anacarde.domain.dto.write.user.update.UserUpdateDto;
import be.labil.anacarde.domain.mapper.UserDetailMapper;
import be.labil.anacarde.domain.mapper.UserListMapper;
import be.labil.anacarde.domain.model.*;
import be.labil.anacarde.infrastructure.persistence.AuctionRepository;
import be.labil.anacarde.infrastructure.persistence.DocumentRepository;
import be.labil.anacarde.infrastructure.persistence.FieldRepository;
import be.labil.anacarde.infrastructure.persistence.user.*;
import be.labil.anacarde.infrastructure.util.PersistenceHelper;
import be.labil.anacarde.infrastructure.util.SecurityHelper;
import be.labil.anacarde.presentation.controller.enums.UserType;
import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserDetailsService, UserService {

	private final ProducerRepository producerRepository;
	private final TransformerRepository transformerRepository;
	private final CarrierRepository carrierRepository;
	private final ExporterRepository exporterRepository;
	private final QualityInspectorRepository qualityInspectorRepository;
	private final FieldRepository fieldRepository;
	private final UserRepository userRepository;
	private final UserDetailMapper userDetailMapper;
	private final AuctionRepository auctionRepository;
	private final StorageService storage;
	private final GeoService geoService;
	private final GoogleAuthServiceImpl googleAuthServiceImpl;

	private final UserListMapper userListMapper;
	private final PasswordEncoder bCryptPasswordEncoder;
	private final PersistenceHelper persistenceHelper;
	private final EntityManager em;
	private final DocumentRepository docRepo;
	private final FieldService fieldService;

	private static final String BENIN_PHONE_COUNTRY_CODE = "+229";
	private static final String BENIN_PHONE_REGEX = "^\\+22901\\d{8}$";

	@Override
	public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
		String trimmedIdentifier = identifier.trim();

		if (trimmedIdentifier.contains("@")) {
			String email = trimmedIdentifier.toLowerCase();
			return userRepository.findByEmail(email)
					.orElseThrow(() -> new UsernameNotFoundException(
							"Utilisateur non trouvé avec l'email : " + email));
		} else {
			String basePourExtractionChiffres = trimmedIdentifier.startsWith("+")
					? trimmedIdentifier.substring(1)
					: trimmedIdentifier;
			String prefixeTelephone = trimmedIdentifier.startsWith("+")
					? "+"
					: BENIN_PHONE_COUNTRY_CODE;

			String chiffresUniquement = basePourExtractionChiffres.replaceAll("[^0-9]", "");
			String normalizedPhone = prefixeTelephone + chiffresUniquement;

			if (!normalizedPhone.matches(BENIN_PHONE_REGEX)) {
				throw new UsernameNotFoundException("Format du numéro de téléphone invalide ("
						+ normalizedPhone + "). Le format attendu pour le Bénin est de type "
						+ BENIN_PHONE_REGEX.replace("\\", "") + ".");
			}

			return userRepository.findByPhone(normalizedPhone)
					.orElseThrow(() -> new UsernameNotFoundException(
							"Utilisateur non trouvé avec le téléphone : " + normalizedPhone));
		}
	}

	@Override
	public UserDetailDto createUser(UserCreateDto dto, List<MultipartFile> files) {
		dto.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));

		dto.setEmail(dto.getEmail().trim().toLowerCase());
		boolean emailExists = userRepository.findByEmail(dto.getEmail()).isPresent();
		boolean phoneExists = userRepository.findByPhone(dto.getPhone()).isPresent();
		boolean agriculturalIdentifierExists = false;
		if (dto instanceof ProducerCreateDto producerDto) {
			agriculturalIdentifierExists = producerRepository
					.findByAgriculturalIdentifier(producerDto.getAgriculturalIdentifier())
					.isPresent();
		}
		if (emailExists || phoneExists || agriculturalIdentifierExists) {
			List<ErrorDetail> errors = new ArrayList<>();
			if (emailExists) {
				errors.add(new ErrorDetail("email", ApiErrorCode.CONFLICT_EMAIL_EXISTS.code(),
						"L'email est déjà utilisé"));
			}
			if (phoneExists) {
				errors.add(new ErrorDetail("phone", ApiErrorCode.CONFLICT_PHONE_EXISTS.code(),
						"Le numéro de téléphone est déjà utilisé"));
			}
			if (agriculturalIdentifierExists) {
				errors.add(new ErrorDetail("agriculturalIdentifier",
						ApiErrorCode.CONFLICT_AGRICULTURAL_ID_EXISTS.code(),
						"L'identifiant agricole est déjà utilisé"));
			}
			throw new ApiErrorException(HttpStatus.CONFLICT, ApiErrorCode.BAD_REQUEST.code(),
					errors);
		}

		User user = userDetailMapper.toEntity(dto);
		if (user.isEnabled() && user.getValidationDate() == null) {
			user.setValidationDate(LocalDateTime.now());
		}
		City city = geoService.findCityById(user.getAddress().getCity().getId());
		Region region = geoService.findRegionByCityId(city);
		user.getAddress().setCity(city);
		user.getAddress().setRegion(region);
		user = userRepository.save(user);

		// création et association d'un champ à la même adresse
		if (dto instanceof ProducerCreateDto producerDto) {
			FieldDto fieldDto = new FieldDto();
			fieldDto.setIdentifier("FIELD-" + user.getId().toString() + "001");
			AddressDto addressDto = AddressDto.builder().cityId(city.getId())
					.regionId(region.getId()).street(user.getAddress().getStreet()).build();
			fieldDto.setAddress(addressDto);
			Producer producer = (Producer) user;
			fieldDto.setProducer(userDetailMapper.toDto(producer));
			fieldService.createField(fieldDto);
		}

		// org.hibernate.TransientPropertyValueException: Not-null property references a transient
		// value - transient instance must be saved before current operation:
		// be.labil.anacarde.domain.model.Field.region -> be.labil.anacarde.domain.model.Region

		// stockage des documents
		if (files != null && !files.isEmpty()) {
			List<Document> saved = storage.storeAll(user, files);
			docRepo.saveAll(saved);
			user.getDocuments().addAll(saved);
		}

		User full = persistenceHelper.saveAndReload(userRepository, user, User::getId);
		return userDetailMapper.toDto(full);
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetailDto getUserById(Integer id) {
		User user = userRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id));
		if (user instanceof Producer producer) {
			Hibernate.initialize(producer.getCooperative());
		}
		return userDetailMapper.toDto(user);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserListDto> listUsers(UserType userType) {
		if (userType != null) {
			switch (userType) {
				case producer -> {
					return producerRepository.findAllByOrderByLastNameAsc().stream()
							.map(userListMapper::toDto).collect(Collectors.toList());
				}
				case transformer -> {
					return transformerRepository.findAllByOrderByLastNameAsc().stream()
							.map(userListMapper::toDto).collect(Collectors.toList());
				}
				case quality_inspector -> {
					return qualityInspectorRepository.findAllByOrderByLastNameAsc().stream()
							.map(userListMapper::toDto).collect(Collectors.toList());
				}
				case exporter -> {
					return exporterRepository.findAllByOrderByLastNameAsc().stream()
							.map(userListMapper::toDto).collect(Collectors.toList());
				}
				case carrier -> {
					return carrierRepository.findAllByOrderByLastNameAsc().stream()
							.map(userListMapper::toDto).collect(Collectors.toList());
				}
				default -> throw new ApiErrorException(HttpStatus.BAD_REQUEST,
						ApiErrorCode.BAD_REQUEST.code(), List.of(new ErrorDetail("userType",
								"user.type", "Type utilisateur inconnu")));
			}
		}
		return userRepository.findAllByOrderByLastNameAsc().stream().map(userListMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public UserDetailDto updateUser(Integer id, UserUpdateDto userUpdateDto) {
		User existingUser = userRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

		User authenticatedUser = SecurityHelper.getAuthenticatedUserOrFail();

		boolean isAdmin = SecurityHelper.isAdmin(authenticatedUser);
		boolean isSelfUpdate = authenticatedUser.getId().equals(existingUser.getId());

		if (!isAdmin && !isSelfUpdate) {
			throw new ApiErrorException(HttpStatus.FORBIDDEN, ApiErrorCode.ACCESS_FORBIDDEN.code(),
					List.of(new ErrorDetail("user", "user.update.forbidden",
							"Vous n'êtes pas autorisé à modifier cet utilisateur.")));
		}

		if (!isAdmin) {
			userUpdateDto.setEnabled(null);
		}

		// Mets uniquement à jour les champs non nuls du DTO
		if (userUpdateDto.getEmail() != null && !userUpdateDto.getEmail().isBlank())
			userUpdateDto.setEmail(userUpdateDto.getEmail().trim().toLowerCase());
		User user = userDetailMapper.partialUpdate(userUpdateDto, existingUser);

		// Gestion des coopératives.
		if (userUpdateDto instanceof ProducerUpdateDto producerDto && user instanceof Producer) {
			if (producerDto.getCooperativeId() != null) {
				Cooperative reference = em.getReference(Cooperative.class,
						producerDto.getCooperativeId());
				((Producer) user).setCooperative(reference);
			} else {
				((Producer) user).setCooperative(null);
			}
		}

		// Mise à jour de l'adresse si fournie
		if (user.getAddress() != null && user.getAddress().getCity() != null
				&& user.getAddress().getCity().getId() != null) {
			City city = geoService.findCityById(user.getAddress().getCity().getId());
			user.getAddress().setCity(city);
			user.getAddress().setRegion(geoService.findRegionByCityId(city));
		}

		if (user.isEnabled() && user.getValidationDate() == null) {
			user.setValidationDate(LocalDateTime.now());
		}

		if (userUpdateDto.getPassword() != null && !userUpdateDto.getPassword().isBlank()) {
			user.setPassword(bCryptPasswordEncoder.encode(userUpdateDto.getPassword()));
		}

		User full = persistenceHelper.saveAndReload(userRepository, user, User::getId);
		return userDetailMapper.toDto(full);
	}

	@Override
	public void deleteUser(Integer id) {
		User user = userRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

		if (auctionRepository.existsByTraderId(user.getId())) {
			throw new OperationNotAllowedException(
					"L'utilisateur ne peut pas être supprimé car il est associé à des enchères.");
		}

		if (user instanceof Producer producer) {
			List<Field> byProducerId = fieldRepository.findByProducerId(producer.getId());
			fieldRepository.deleteAll(byProducerId);
		}

		userRepository.deleteById(id);
	}

	@Override
	public boolean emailExists(String email) {
		return userRepository.existsByEmail(email.trim().toLowerCase());
	}

	@Override
	public boolean phoneExists(String phone) {
		return userRepository.existsByPhone(phone);
	}

	@Override
	public User authenticateWithGoogle(String googleToken)
			throws GeneralSecurityException, IOException {
		return googleAuthServiceImpl.processGoogleRegistration(googleToken);
	}
}