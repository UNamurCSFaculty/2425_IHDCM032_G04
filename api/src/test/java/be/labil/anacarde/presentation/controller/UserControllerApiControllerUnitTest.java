package be.labil.anacarde.presentation.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.application.service.UserService;
import be.labil.anacarde.domain.dto.UserDetailDto;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Tests unitaires pour le contrôleur des utilisateurs. */
@ExtendWith(MockitoExtension.class)
public class UserControllerApiControllerUnitTest {
	@Mock
	private UserService userService;

	@InjectMocks
	private UserControllerApiController userController;

	@BeforeEach
	public void setUp() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/api/users");
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
	}

	/** Teste la récupération d'un utilisateur existant. */
	@Test
	public void testGetUser() {
		UserDetailDto userDetailDto = new UserDetailDto();
		userDetailDto.setId(1);
		userDetailDto.setFirstName("John");
		userDetailDto.setLastName("Doe");
		userDetailDto.setEmail("john.doe@example.com");

		when(userService.getUserById(1)).thenReturn(userDetailDto);

		ResponseEntity<UserDetailDto> response = userController.getUser(1);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("John", response.getBody().getFirstName());
	}

	/** Teste la création d'un nouvel utilisateur. */
	@Test
	public void testCreateUser() {
		UserDetailDto inputDto = new UserDetailDto();
		inputDto.setFirstName("Alice");
		inputDto.setLastName("Smith");
		inputDto.setEmail("alice.smith@example.com");
		inputDto.setPassword("secret");

		UserDetailDto createdDto = new UserDetailDto();
		createdDto.setId(1);
		createdDto.setFirstName("Alice");
		createdDto.setLastName("Smith");
		createdDto.setEmail("alice.smith@example.com");

		when(userService.createUser(any(UserDetailDto.class))).thenReturn(createdDto);

		ResponseEntity<UserDetailDto> response = userController.createUser(inputDto);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("Alice", response.getBody().getFirstName());
	}

	/** Teste la mise à jour d'un utilisateur existant. */
	@Test
	public void testUpdateUser() {
		UserDetailDto updateDto = new UserDetailDto();
		updateDto.setFirstName("John Updated");
		updateDto.setLastName("Doe Updated");
		updateDto.setEmail("john.updated@example.com");
		updateDto.setPassword("newpassword");

		UserDetailDto updatedDto = new UserDetailDto();
		updatedDto.setId(1);
		updatedDto.setFirstName("John Updated");
		updatedDto.setLastName("Doe Updated");
		updatedDto.setEmail("john.updated@example.com");

		when(userService.updateUser(eq(1), any(UserDetailDto.class))).thenReturn(updatedDto);

		ResponseEntity<UserDetailDto> response = userController.updateUser(1, updateDto);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("John Updated", response.getBody().getFirstName());
	}

	/** Teste la suppression d'un utilisateur. */
	@Test
	public void testDeleteUser() {
		doNothing().when(userService).deleteUser(1);

		ResponseEntity<Void> response = userController.deleteUser(1);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	/** Teste la mise à jour des rôles d'un utilisateur avec succès. */
	@Test
	public void testUpdateUserRolesSuccess() {
		List<String> roleNames = List.of("ROLE_USER");

		UserDetailDto updatedDto = new UserDetailDto();
		updatedDto.setId(1);
		updatedDto.setEmail("john.doe@example.com");
		updatedDto.setFirstName("John");
		updatedDto.setLastName("Doe");

		when(userService.updateUserRoles(eq(1), any(List.class))).thenReturn(updatedDto);

		ResponseEntity<UserDetailDto> response = userController.updateUserRoles(1, roleNames);

		// Vérifie le code de réponse et le contenu
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(updatedDto.getId(), response.getBody().getId());
		assertEquals("john.doe@example.com", response.getBody().getEmail());
	}

	/** Teste la mise à jour des rôles d'un utilisateur lorsque l'utilisateur n'est pas trouvé. */
	@Test
	public void testUpdateUserRolesUserNotFound() {
		List<String> roleNames = List.of("ROLE_USER");

		when(userService.updateUserRoles(eq(999), any(List.class)))
				.thenThrow(new ResourceNotFoundException("Utilisateur non trouvé"));

		assertThrows(ResourceNotFoundException.class, () -> userController.updateUserRoles(999, roleNames));
	}
}
