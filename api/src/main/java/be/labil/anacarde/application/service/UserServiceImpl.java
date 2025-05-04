package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.*;
import be.labil.anacarde.domain.dto.user.ProducerDetailDto;
import be.labil.anacarde.domain.dto.user.UserDetailDto;
import be.labil.anacarde.domain.dto.user.UserListDto;
import be.labil.anacarde.domain.mapper.UserDetailMapper;
import be.labil.anacarde.domain.mapper.UserListMapper;
import be.labil.anacarde.domain.model.Role;
import be.labil.anacarde.domain.model.User;
import be.labil.anacarde.infrastructure.persistence.RoleRepository;
import be.labil.anacarde.infrastructure.persistence.user.UserRepository;
import java.time.LocalDateTime;
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

	private final UserRepository userRepository;
	private final UserDetailMapper userDetailMapper;

	private final UserListMapper userListMapper;
	private final PasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email));
	}

	@Override
	public UserDetailDto createUser(UserDetailDto dto) throws BadRequestException {
		dto.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));

		boolean emailExist = userRepository.findByEmail(dto.getEmail()).isPresent();
		boolean phoneExist = userRepository.findByPhone(dto.getPhone()).isPresent();
		// TODO: vérifier l'identificateur ID pour un producteur.
		if (emailExist || phoneExist) {
			List<ErrorDetail> errors = new ArrayList<>();
			if (emailExist) {
				errors.add(new ErrorDetail("email", ApiErrorCode.CONFLICT_EMAIL_EXISTS.code(),
						"L'email est déjà utilisé"));
			}
			if (phoneExist) {
				errors.add(new ErrorDetail("phone", ApiErrorCode.CONFLICT_PHONE_EXISTS.code(),
						"Le numéro de téléphone est déjà utilisé"));
			}
			throw new ApiErrorException(HttpStatus.CONFLICT, ApiErrorCode.BAD_REQUEST.code(), errors);
		}

		User user;
		if (dto instanceof ProducerDetailDto producerDto) {
			user = userDetailMapper.toEntity(producerDto);
		} else {
			user = userDetailMapper.toEntity(dto);
		}
		user.setRegistrationDate(LocalDateTime.now());
		User saved = userRepository.save(user);
		return userDetailMapper.toDto(saved);
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
	public UserDetailDto updateUser(Integer id, UserDetailDto userDetailDto) {
		User existingUser = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
		// Mets uniquement à jour les champs non nuls du DTO
		User updatedUser = userDetailMapper.partialUpdate(userDetailDto, existingUser);

		if (userDetailDto.getPassword() != null && !userDetailDto.getPassword().isBlank()) {
			updatedUser.setPassword(bCryptPasswordEncoder.encode(userDetailDto.getPassword()));
		}

		User saved = userRepository.save(updatedUser);
		return userDetailMapper.toDto(saved);
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
