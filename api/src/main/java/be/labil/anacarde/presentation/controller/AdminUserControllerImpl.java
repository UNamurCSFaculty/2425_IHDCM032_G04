package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.UserService;
import be.labil.anacarde.domain.dto.db.user.UserDetailDto;
import be.labil.anacarde.domain.dto.db.user.UserListDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminUserControllerImpl implements AdminUserController {

	private final UserService userService;

	@Override
	public ResponseEntity<List<? extends UserListDto>> listUsers() {
		List<UserListDto> users = userService.listUsers();
		return ResponseEntity.ok(users);
	}

	@Override
	public ResponseEntity<? extends UserDetailDto> getUser(Integer id) {
		UserDetailDto user = userService.getUserById(id);
		return ResponseEntity.ok(user);
	}
}
