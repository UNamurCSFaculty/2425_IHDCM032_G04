package be.labil.anacarde.application.service.impl;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.application.service.UserService;
import be.labil.anacarde.domain.dto.UserDto;
import be.labil.anacarde.domain.mapper.UserMapper;
import be.labil.anacarde.domain.model.Role;
import be.labil.anacarde.domain.model.User;
import be.labil.anacarde.infrastructure.persistence.RoleRepository;
import be.labil.anacarde.infrastructure.persistence.UserRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
/**
 * Ce service fournit des méthodes pour l'authentification et la gestion des utilisateurs, incluant la création, la
 * récupération, la mise à jour et la suppression des utilisateurs. Il utilise un UserRepository pour la persistance, un
 * UserMapper pour la conversion entre entités et DTO, et un PasswordEncoder pour sécuriser les mots de passe.
 */
public class UserDetailServiceImpl implements UserDetailsService, UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email));
	}

	@Override
	public UserDto createUser(UserDto userDto) {
		if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
			userDto.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
		}
		User user = userMapper.toEntity(userDto);
		User saved = userRepository.save(user);
		return userMapper.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public UserDto getUserById(Integer id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
		return userMapper.toDto(user);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserDto> listUsers() {
		return userRepository.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public UserDto updateUser(Integer id, UserDto userDto) {
		User existingUser = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
		// Mets uniquement à jour les champs non nuls du DTO
		User updatedUser = userMapper.partialUpdate(userDto, existingUser);

		if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
			updatedUser.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
		}

		User saved = userRepository.save(updatedUser);
		return userMapper.toDto(saved);
	}

	@Override
	public void deleteUser(Integer id) {
		if (!userRepository.existsById(id)) {
			throw new ResourceNotFoundException("Utilisateur non trouvé");
		}
		userRepository.deleteById(id);
	}

	@Override
	public UserDto addRoleToUser(Integer userId, String roleName) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
		Role role = roleRepository.findByName(roleName)
				.orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé"));
		user.getRoles().add(role);

		User savedUser = userRepository.save(user);
		return userMapper.toDto(savedUser);
	}

	@Override
	public UserDto updateUserRoles(Integer userId, List<String> roleNames) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

		Set<Role> newRoles = roleNames.stream()
				.map(roleName -> roleRepository.findByName(roleName)
						.orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé: " + roleName)))
				.collect(Collectors.toSet());

		user.setRoles(newRoles);

		User savedUser = userRepository.save(user);
		return userMapper.toDto(savedUser);
	}
}
