package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.UserService;
import be.labil.anacarde.domain.dto.user.UserDetailDto;
import be.labil.anacarde.domain.dto.user.UserListDto;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class UserControllerApiController implements UserApi {

	private final UserService userService;

	@Override
	public ResponseEntity<? extends UserDetailDto> getUser(Integer id) {
		UserDetailDto user = userService.getUserById(id);
		return ResponseEntity.ok(user);
	}

	@Override
	public ResponseEntity<UserDetailDto> createUser(UserDetailDto userDetailDto) {
		UserDetailDto created = userService.createUser(userDetailDto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId())
				.toUri();
		return ResponseEntity.created(location).body(created);
	}

	@Override
	public ResponseEntity<List<? extends UserListDto>> listUsers() {
		List<UserListDto> users = userService.listUsers();
		return ResponseEntity.ok(users);
	}

	@Override
	public ResponseEntity<UserDetailDto> updateUser(Integer id, UserDetailDto userDetailDto) {
		UserDetailDto updated = userService.updateUser(id, userDetailDto);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<Void> deleteUser(Integer id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<? extends UserDetailDto> addRoleToUser(Integer id, String roleName) {
		UserDetailDto updated = userService.addRoleToUser(id, roleName);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<? extends UserDetailDto> updateUserRoles(Integer id, List<String> roleNames) {
		UserDetailDto updated = userService.updateUserRoles(id, roleNames);
		return ResponseEntity.ok(updated);
	}
}
