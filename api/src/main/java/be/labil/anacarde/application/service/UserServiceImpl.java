package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.*;
import be.labil.anacarde.domain.dto.db.user.UserDetailDto;
import be.labil.anacarde.domain.dto.db.user.UserListDto;
import be.labil.anacarde.domain.dto.write.user.ProducerUpdateDto;
import be.labil.anacarde.domain.dto.write.user.UserUpdateDto;
import be.labil.anacarde.domain.mapper.UserDetailMapper;
import be.labil.anacarde.domain.mapper.UserListMapper;
import be.labil.anacarde.domain.model.Role;
import be.labil.anacarde.domain.model.User;
import be.labil.anacarde.infrastructure.persistence.RoleRepository;
import be.labil.anacarde.infrastructure.persistence.user.ProducerRepository;
import be.labil.anacarde.infrastructure.persistence.user.UserRepository;
import be.labil.anacarde.infrastructure.util.PersistenceHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {

	private final RoleRepository roleRepository;
	private final ProducerRepository producerRepository;
	private final UserRepository userRepository;
	private final UserDetailMapper userDetailMapper;

	private final UserListMapper userListMapper;
	private final PasswordEncoder bCryptPasswordEncoder;
	private final PersistenceHelper persistenceHelper;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email));
	}

	@Override
	public UserDetailDto createUser(UserUpdateDto dto) throws BadRequestException {
		dto.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));

		boolean emailExists = userRepository.findByEmail(dto.getEmail()).isPresent();
		boolean phoneExists = userRepository.findByPhone(dto.getPhone()).isPresent();
		boolean agriculturalIdentifierExists = false;
		if (dto instanceof ProducerUpdateDto producerDto) {
			agriculturalIdentifierExists = producerRepository
					.findByAgriculturalIdentifier(producerDto.getAgriculturalIdentifier()).isPresent();
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
				errors.add(
						new ErrorDetail("agriculturalIdentifier", ApiErrorCode.CONFLICT_AGRICULTURAL_ID_EXISTS.code(),
								"L'identifiant agricole est déjà utilisé"));
			}
			throw new ApiErrorException(HttpStatus.CONFLICT, ApiErrorCode.BAD_REQUEST.code(), errors);
		}

		User user = userDetailMapper.toEntity(dto);
		User full = persistenceHelper.saveAndReload(userRepository, user, User::getId);
		return userDetailMapper.toDto(full);
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetailDto getUserById(Integer id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
		return userDetailMapper.toDto(user);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserListDto> listUsers() {
		return userRepository.findAll().stream().map(userListMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public UserDetailDto updateUser(Integer id, UserUpdateDto userDetailDto) {
		User existingUser = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
		// Mets uniquement à jour les champs non nuls du DTO
		User updatedUser = userDetailMapper.partialUpdate(userDetailDto, existingUser);

		if (userDetailDto.getPassword() != null && !userDetailDto.getPassword().isBlank()) {
			updatedUser.setPassword(bCryptPasswordEncoder.encode(userDetailDto.getPassword()));
		}

		User full = persistenceHelper.saveAndReload(userRepository, updatedUser, User::getId);
		return userDetailMapper.toDto(full);
	}

	@Override
	public void deleteUser(Integer id) {
		if (!userRepository.existsById(id)) {
			throw new ResourceNotFoundException("Utilisateur non trouvé");
		}
		userRepository.deleteById(id);
	}

	@Override
	public UserDetailDto addRoleToUser(Integer userId, String roleName) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
		Role role = roleRepository.findByName(roleName)
				.orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé"));

		if (user.getRoles().contains(role)) {
			throw new BadRequestException("Le rôle est déjà attribué à l'utilisateur");
		}
		user.getRoles().add(role);

		User savedUser = userRepository.save(user);
		return userDetailMapper.toDto(savedUser);
	}

	@Override
	public UserDetailDto updateUserRoles(Integer userId, List<String> roleNames) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

		Set<Role> newRoles = roleNames.stream()
				.map(roleName -> roleRepository.findByName(roleName)
						.orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé: " + roleName)))
				.collect(Collectors.toSet());

		user.setRoles(newRoles);

		User savedUser = userRepository.save(user);
		return userDetailMapper.toDto(savedUser);
	}
}
