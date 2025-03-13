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
public class UserDetailServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserDetailServiceImpl userDetailServiceImpl;

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

        // Assume the mapper converts between User and UserDto directly
        Mockito.lenient().when(userMapper.toEntity(any(UserDto.class))).thenReturn(user);
        Mockito.lenient().when(userMapper.toDto(any(User.class))).thenReturn(userDto);
    }

    // Test loadUserByUsername with valid email
    @Test
    void testLoadUserByUsernameSuccess() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        assertThat(userDetailServiceImpl.loadUserByUsername("test@example.com")).isEqualTo(user);
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    // Test loadUserByUsername with non-existing email
    @Test
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        assertThatThrownBy(
                        () -> userDetailServiceImpl.loadUserByUsername("nonexistent@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User Not Found with email");
    }

    // Test createUser: valid user creation with password encoding
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

    // Test getUserById: user exists
    @Test
    void testGetUserByIdSuccess() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        UserDto result = userDetailServiceImpl.getUserById(1);
        assertThat(result).isEqualTo(userDto);
    }

    // Test getUserById: user does not exist
    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userDetailServiceImpl.getUserById(1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }

    // Test listUsers: returns list of users
    @Test
    void testListUsers() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        List<UserDto> users = userDetailServiceImpl.listUsers();
        assertThat(users).hasSize(1).contains(userDto);
    }

    // Test updateUser: successful update
    @Test
    void testUpdateUser() {
        // Assume updated DTO has a new password and some field changes
        UserDto updateDto = new UserDto();
        updateDto.setPassword("newPassword");
        updateDto.setEmail("updated@example.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        when(userMapper.partialUpdate(any(UserDto.class), any(User.class)))
                .thenAnswer(
                        invocation -> {
                            UserDto dto = invocation.getArgument(0);
                            User userToUpdate = invocation.getArgument(1);
                            if (dto.getEmail() != null) {
                                userToUpdate.setEmail(dto.getEmail());
                            }
                            return userToUpdate;
                        });

        when(userMapper.toDto(any(User.class)))
                .thenAnswer(
                        invocation -> {
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

    // Test deleteUser: successful deletion
    @Test
    void testDeleteUserSuccess() {
        when(userRepository.existsById(1)).thenReturn(true);
        userDetailServiceImpl.deleteUser(1);
        verify(userRepository, times(1)).deleteById(1);
    }

    // Test deleteUser: deletion of non-existing user
    @Test
    void testDeleteUserNotFound() {
        when(userRepository.existsById(1)).thenReturn(false);
        assertThatThrownBy(() -> userDetailServiceImpl.deleteUser(1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }

    // Test addRoleToUser: successful role addition
    @Test
    void testAddRoleToUserSuccess() {
        // Set up a mutable set of roles for the user
        user.setRoles(new java.util.HashSet<>());
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(user);
        // Assume mapper returns the same DTO for simplicity
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userDetailServiceImpl.addRoleToUser(1, "ROLE_USER");
        verify(userRepository, times(1)).findById(1);
        verify(roleRepository, times(1)).findByName("ROLE_USER");
        verify(userRepository, times(1)).save(user);
        assertThat(result).isEqualTo(userDto);
    }

    // Test addRoleToUser: non-existing user
    @Test
    void testAddRoleToUserUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userDetailServiceImpl.addRoleToUser(1, "ROLE_USER"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }

    // Test addRoleToUser: non-existing role
    @Test
    void testAddRoleToUserRoleNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userDetailServiceImpl.addRoleToUser(1, "ROLE_USER"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Role not found");
    }

    // Test for successful update of user roles
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

    // Test when a role is not found during update
    @Test
    void testUpdateUserRolesRoleNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.empty());

        List<String> roleNames = List.of("ROLE_ADMIN");

        assertThatThrownBy(() -> userDetailServiceImpl.updateUserRoles(1, roleNames))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Role not found");
    }

    // Test addRoleToUser: non-existing user
    @Test
    void testUpdateRolesRoleToUserUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userDetailServiceImpl.updateUserRoles(1, List.of()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }
}
