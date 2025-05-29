package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorCode;
import be.labil.anacarde.application.exception.ApiErrorException;
import be.labil.anacarde.application.exception.ErrorDetail;
import be.labil.anacarde.application.service.UserService;
import be.labil.anacarde.domain.dto.db.user.UserDetailDto;
import be.labil.anacarde.domain.dto.db.user.UserListDto;
import be.labil.anacarde.domain.dto.write.user.create.AdminCreateDto;
import be.labil.anacarde.domain.dto.write.user.create.UserCreateDto;
import be.labil.anacarde.domain.dto.write.user.update.UserUpdateDto;
import be.labil.anacarde.presentation.controller.enums.UserType;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class UserApiController implements UserApi {

	private final UserService userService;

	@Override
	public ResponseEntity<UserDetailDto> createUser(UserCreateDto user,
			List<MultipartFile> documents) {
		if (user instanceof AdminCreateDto admin) {
			throw new ApiErrorException(HttpStatus.FORBIDDEN, ApiErrorCode.ACCESS_FORBIDDEN.code(),
					List.of(new ErrorDetail("user", "user.admin.not.allowed",
							"Il est interdit de créer un utilisateur admin via cette API.")));
		}
		if (documents == null || documents.isEmpty()) {
			throw new ApiErrorException(HttpStatus.BAD_REQUEST, ApiErrorCode.BAD_REQUEST.code(),
					List.of(new ErrorDetail("documents", "user.documents.required",
							"Le champ documents est requis.")));
		}
		UserDetailDto created = userService.createUser(user, documents);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(created.getId()).toUri();

		return ResponseEntity.created(location).body(created);
	}

	@Override
	public ResponseEntity<UserDetailDto> updateUser(Integer id, UserUpdateDto userUpdateDto) {
		UserDetailDto updated = userService.updateUser(id, userUpdateDto);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<Void> deleteUser(Integer id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}

	// Vérification de l'existence de l'email et du téléphone
	@Override // new
	public ResponseEntity<Boolean> checkEmail(String email) {
		boolean exists = userService.emailExists(email);
		return ResponseEntity.ok(exists);
	}

	@Override
	public ResponseEntity<Boolean> checkPhone(String phone) {
		boolean exists = userService.phoneExists(phone);
		return ResponseEntity.ok(exists);
	}

	@Override
	public ResponseEntity<List<? extends UserListDto>> listUsers(UserType type) {
		List<UserListDto> users = userService.listUsers(type);
		return ResponseEntity.ok(users);
	}
}
