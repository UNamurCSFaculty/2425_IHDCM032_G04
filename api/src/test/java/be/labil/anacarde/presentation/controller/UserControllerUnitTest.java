package be.labil.anacarde.presentation.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.application.service.UserService;
import be.labil.anacarde.domain.dto.UserDto;
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

@ExtendWith(MockitoExtension.class)
public class UserControllerUnitTest {
    @Mock private UserService userService;

    @InjectMocks private UserController userController;

    @BeforeEach
    public void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/users");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    public void testGetUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");

        when(userService.getUserById(1)).thenReturn(userDto);

        ResponseEntity<UserDto> response = userController.getUser(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John", response.getBody().getFirstName());
    }

    @Test
    public void testCreateUser() {
        UserDto inputDto = new UserDto();
        inputDto.setFirstName("Alice");
        inputDto.setLastName("Smith");
        inputDto.setEmail("alice.smith@example.com");
        inputDto.setPassword("secret");

        UserDto createdDto = new UserDto();
        createdDto.setId(1);
        createdDto.setFirstName("Alice");
        createdDto.setLastName("Smith");
        createdDto.setEmail("alice.smith@example.com");

        when(userService.createUser(any(UserDto.class))).thenReturn(createdDto);

        ResponseEntity<UserDto> response = userController.createUser(inputDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Alice", response.getBody().getFirstName());
    }

    @Test
    public void testUpdateUser() {
        UserDto updateDto = new UserDto();
        updateDto.setFirstName("John Updated");
        updateDto.setLastName("Doe Updated");
        updateDto.setEmail("john.updated@example.com");
        updateDto.setPassword("newpassword");

        UserDto updatedDto = new UserDto();
        updatedDto.setId(1);
        updatedDto.setFirstName("John Updated");
        updatedDto.setLastName("Doe Updated");
        updatedDto.setEmail("john.updated@example.com");

        when(userService.updateUser(eq(1), any(UserDto.class))).thenReturn(updatedDto);

        ResponseEntity<UserDto> response = userController.updateUser(1, updateDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John Updated", response.getBody().getFirstName());
    }

    @Test
    public void testDeleteUser() {
        doNothing().when(userService).deleteUser(1);

        ResponseEntity<Void> response = userController.deleteUser(1);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testUpdateUserRolesSuccess() {
        List<String> roleNames = List.of("ROLE_USER");

        UserDto updatedDto = new UserDto();
        updatedDto.setId(1);
        updatedDto.setEmail("john.doe@example.com");
        updatedDto.setFirstName("John");
        updatedDto.setLastName("Doe");

        when(userService.updateUserRoles(eq(1), any(List.class))).thenReturn(updatedDto);

        ResponseEntity<UserDto> response = userController.updateUserRoles(1, roleNames);

        // Verify response status and content
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedDto.getId(), response.getBody().getId());
        assertEquals("john.doe@example.com", response.getBody().getEmail());
    }

    // Test for update user roles when user is not found via the controller
    @Test
    public void testUpdateUserRolesUserNotFound() {
        List<String> roleNames = List.of("ROLE_USER");

        when(userService.updateUserRoles(eq(999), any(List.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        assertThrows(
                ResourceNotFoundException.class,
                () -> {
                    userController.updateUserRoles(999, roleNames);
                });
    }
}
