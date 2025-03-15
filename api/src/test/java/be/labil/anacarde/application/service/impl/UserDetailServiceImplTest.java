package be.labil.anacarde.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.UserDto;
import be.labil.anacarde.domain.mapper.UserMapper;
import be.labil.anacarde.domain.model.Role;
import be.labil.anacarde.domain.model.User;
import be.labil.anacarde.infrastructure.persistence.RoleRepository;
import be.labil.anacarde.infrastructure.persistence.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
/**
 * Teste la classe UserDetailServiceImpl.
 *
 * <p>
 * Cette classe effectue des tests unitaires sur les méthodes de UserDetailServiceImpl, telles que la récupération d'un
 * utilisateur par email, la création, la mise à jour, la suppression, et l'ajout ou la mise à jour des rôles d'un
 * utilisateur.
 */
public class UserDetailServiceImplTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private RoleRepository roleRepository;
	@Mock
	private UserMapper userMapper;
	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserDetailServiceImpl userDetailServiceImpl;

	private User user;
	private UserDto userDto;
	private Role role;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setId(1);
		user.setEmail("test@example.com");
		user.setPassword("rawPassword");
		user.setRoles(Collections.emptySet());

		userDto = new UserDto();
		userDto.setId(1);
		userDto.setEmail("test@example.com");
		userDto.setPassword("rawPassword");

		role = new Role();
		role.setId(100);
		role.setName("ROLE_USER");

		// On suppose que le mapper convertit directement entre User et UserDto.
		Mockito.lenient().when(userMapper.toEntity(any(UserDto.class))).thenReturn(user);
		Mockito.lenient().when(userMapper.toDto(any(User.class))).thenReturn(userDto);
	}

	/** Test de la méthode loadUserByUsername avec un email valide. */
	@Test
	void testLoadUserByUsernameSuccess() {
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
		assertThat(userDetailServiceImpl.loadUserByUsername("test@example.com")).isEqualTo(user);
		verify(userRepository, times(1)).findByEmail("test@example.com");
	}

	/** Test de la méthode loadUserByUsername avec un email inexistant. */
	@Test
	void testLoadUserByUsernameNotFound() {
		when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
		assertThatThrownBy(() -> userDetailServiceImpl.loadUserByUsername("nonexistent@example.com"))
				.isInstanceOf(UsernameNotFoundException.class)
				.hasMessageContaining("Utilisateur non trouvé avec l'email");
	}

	/**
	 * Test de la création d'un utilisateur : vérifie que le mot de passe est encodé correctement et que l'utilisateur
	 * est sauvegardé.
	 */
	@Test
	void testCreateUser() {
		String encodedPassword = "encodedPassword";
		when(passwordEncoder.encode("rawPassword")).thenReturn(encodedPassword);
		userDto.setPassword("rawPassword");
		when(userRepository.save(user)).thenReturn(user);

		UserDto result = userDetailServiceImpl.createUser(userDto);
		verify(passwordEncoder, times(1)).encode("rawPassword");
		verify(userRepository, times(1)).save(user);
		assertThat(result).isEqualTo(userDto);
	}

	/** Test de la récupération d'un utilisateur existant par son identifiant. */
	@Test
	void testGetUserByIdSuccess() {
		when(userRepository.findById(1)).thenReturn(Optional.of(user));
		UserDto result = userDetailServiceImpl.getUserById(1);
		assertThat(result).isEqualTo(userDto);
	}

	/**
	 * Test de la récupération d'un utilisateur par son identifiant quand l'utilisateur n'existe pas.
	 */
	@Test
	void testGetUserByIdNotFound() {
		when(userRepository.findById(1)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> userDetailServiceImpl.getUserById(1)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Utilisateur non trouvé");
	}

	/** Test de la méthode listUsers qui doit retourner la liste de tous les utilisateurs. */
	@Test
	void testListUsers() {
		when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
		List<UserDto> users = userDetailServiceImpl.listUsers();
		assertThat(users).hasSize(1).contains(userDto);
	}

	/**
	 * Test de la mise à jour d'un utilisateur existant.
	 *
	 * <p>
	 * On suppose que le DTO mis à jour comporte un nouveau mot de passe et des modifications sur d'autres champs.
	 */
	@Test
	void testUpdateUser() {
		UserDto updateDto = new UserDto();
		updateDto.setPassword("newPassword");
		updateDto.setEmail("updated@example.com");

		when(userRepository.findById(1)).thenReturn(Optional.of(user));

		when(userMapper.partialUpdate(any(UserDto.class), any(User.class))).thenAnswer(invocation -> {
			UserDto dto = invocation.getArgument(0);
			User userToUpdate = invocation.getArgument(1);
			if (dto.getEmail() != null) {
				userToUpdate.setEmail(dto.getEmail());
			}
			return userToUpdate;
		});

		when(userMapper.toDto(any(User.class))).thenAnswer(invocation -> {
			User updatedUser = invocation.getArgument(0);
			UserDto dto = new UserDto();
			dto.setEmail(updatedUser.getEmail());
			dto.setPassword(updatedUser.getPassword());
			return dto;
		});

		when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
		when(userRepository.save(user)).thenReturn(user);

		UserDto result = userDetailServiceImpl.updateUser(1, updateDto);
		assertThat(result.getEmail()).isEqualTo("updated@example.com");
		verify(passwordEncoder, times(1)).encode("newPassword");
	}

	/** Test de la suppression réussie d'un utilisateur existant. */
	@Test
	void testDeleteUserSuccess() {
		when(userRepository.existsById(1)).thenReturn(true);
		userDetailServiceImpl.deleteUser(1);
		verify(userRepository, times(1)).deleteById(1);
	}

	/** Test de la suppression d'un utilisateur inexistant. */
	@Test
	void testDeleteUserNotFound() {
		when(userRepository.existsById(1)).thenReturn(false);
		assertThatThrownBy(() -> userDetailServiceImpl.deleteUser(1)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Utilisateur non trouvé");
	}

	/** Test de l'ajout d'un rôle à un utilisateur avec succès. */
	@Test
	void testAddRoleToUserSuccess() {
		// Prépare un ensemble de rôles mutable pour l'utilisateur
		user.setRoles(new java.util.HashSet<>());
		when(userRepository.findById(1)).thenReturn(Optional.of(user));
		when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
		when(userRepository.save(user)).thenReturn(user);
		// On suppose que le mapper retourne le même DTO pour simplifier
		when(userMapper.toDto(user)).thenReturn(userDto);

		UserDto result = userDetailServiceImpl.addRoleToUser(1, "ROLE_USER");
		verify(userRepository, times(1)).findById(1);
		verify(roleRepository, times(1)).findByName("ROLE_USER");
		verify(userRepository, times(1)).save(user);
		assertThat(result).isEqualTo(userDto);
	}

	/** Test de l'ajout d'un rôle à un utilisateur inexistant. */
	@Test
	void testAddRoleToUserUserNotFound() {
		when(userRepository.findById(1)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> userDetailServiceImpl.addRoleToUser(1, "ROLE_USER"))
				.isInstanceOf(ResourceNotFoundException.class).hasMessage("Utilisateur non trouvé");
	}

	/** Test de l'ajout d'un rôle inexistant à un utilisateur. */
	@Test
	void testAddRoleToUserRoleNotFound() {
		when(userRepository.findById(1)).thenReturn(Optional.of(user));
		when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());
		assertThatThrownBy(() -> userDetailServiceImpl.addRoleToUser(1, "ROLE_USER"))
				.isInstanceOf(ResourceNotFoundException.class).hasMessage("Rôle non trouvé");
	}

	/** Test de la mise à jour réussie des rôles d'un utilisateur. */
	@Test
	void testUpdateUserRolesSuccess() {
		user.setRoles(new java.util.HashSet<>());

		when(userRepository.findById(1)).thenReturn(Optional.of(user));
		when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
		when(userRepository.save(user)).thenReturn(user);
		when(userMapper.toDto(user)).thenReturn(userDto);

		List<String> roleNames = List.of("ROLE_USER");
		UserDto result = userDetailServiceImpl.updateUserRoles(1, roleNames);

		verify(userRepository, times(1)).findById(1);
		verify(roleRepository, times(1)).findByName("ROLE_USER");
		verify(userRepository, times(1)).save(user);
		assertThat(result).isEqualTo(userDto);
	}

	/** Test de la mise à jour des rôles d'un utilisateur lorsque l'un des rôles est introuvable. */
	@Test
	void testUpdateUserRolesRoleNotFound() {
		when(userRepository.findById(1)).thenReturn(Optional.of(user));
		when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.empty());

		List<String> roleNames = List.of("ROLE_ADMIN");

		assertThatThrownBy(() -> userDetailServiceImpl.updateUserRoles(1, roleNames))
				.isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("Rôle non trouvé");
	}

	/** Test de la mise à jour des rôles d'un utilisateur lorsque l'utilisateur est introuvable. */
	@Test
	void testUpdateRolesRoleToUserUserNotFound() {
		when(userRepository.findById(1)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> userDetailServiceImpl.updateUserRoles(1, List.of()))
				.isInstanceOf(ResourceNotFoundException.class).hasMessage("Utilisateur non trouvé");
	}
}
