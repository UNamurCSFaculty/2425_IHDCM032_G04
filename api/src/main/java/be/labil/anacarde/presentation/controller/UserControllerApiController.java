package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.UserService;
import be.labil.anacarde.domain.dto.UserDto;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/** Ce contrôleur implémente l'API de gestion des utilisateurs */
@RestController
@RequiredArgsConstructor
public class UserControllerApiController implements UserApi {

	private final UserService userService;

	@GetMapping("/greeting")
	public ResponseEntity<String> greeting(@RequestParam String name, @RequestParam String lol) {
		String message = "Hello, " + name + "!";
		return ResponseEntity.ok(message);
	}

	@Override
	public ResponseEntity<UserDto> getUser(Integer id) {
		UserDto user = userService.getUserById(id);
		return ResponseEntity.ok(user);
	}

	@Override
	public ResponseEntity<UserDto> createUser(UserDto userDto) {
		UserDto created = userService.createUser(userDto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId())
				.toUri();
		return ResponseEntity.created(location).body(created);
	}

	@Override
	public ResponseEntity<List<UserDto>> listUsers() {
		List<UserDto> users = userService.listUsers();
		return ResponseEntity.ok(users);
	}

	@Override
	public ResponseEntity<UserDto> updateUser(Integer id, UserDto userDto) {
		UserDto updated = userService.updateUser(id, userDto);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<Void> deleteUser(Integer id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<UserDto> addRoleToUser(Integer id, String roleName) {
		UserDto updated = userService.addRoleToUser(id, roleName);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<UserDto> updateUserRoles(Integer id, List<String> roleNames) {
		UserDto updated = userService.updateUserRoles(id, roleNames);
		return ResponseEntity.ok(updated);
	}
}
