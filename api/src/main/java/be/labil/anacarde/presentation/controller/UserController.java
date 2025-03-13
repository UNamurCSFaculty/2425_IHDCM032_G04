package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.UserService;
import be.labil.anacarde.domain.dto.UserDto;
import be.labil.anacarde.domain.dto.ValidationGroups;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/** This controller provides endpoints to retrieve, create, update, and delete users. */
@Tag(name = "Users", description = "API for managing users")
@RestController
@RequestMapping(value = "/api/users", produces = "application/json")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Retrieve detailed information of a user by its ID.
     *
     * @param id The ID of the user.
     * @return ResponseEntity containing the user details.
     */
    @Operation(
            summary = "Get user details",
            description = "Returns the detailed information of a user by its ID.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "User found",
                content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(
            @Parameter(description = "User ID", example = "1", required = true) @PathVariable("id")
                    Integer id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Uses the Create validation group to require the password field.
     *
     * @param userDto The user data to create.
     * @return ResponseEntity containing the created user details.
     */
    @Operation(summary = "Create a user", description = "Creates a new user.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "User created successfully",
                content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Validation error or invalid JSON",
                content = @Content)
    })
    @PostMapping(consumes = "application/json")
    public ResponseEntity<UserDto> createUser(
            @Validated({Default.class, ValidationGroups.Create.class}) @RequestBody
                    UserDto userDto) {
        UserDto created = userService.createUser(userDto);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(created.getId())
                        .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * List all users.
     *
     * @return ResponseEntity containing a list of all users.
     */
    @Operation(summary = "List all users", description = "Returns a list of all users.")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    @GetMapping
    public ResponseEntity<List<UserDto>> listUsers() {
        List<UserDto> users = userService.listUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Uses the Update validation group so that the password becomes optional.
     *
     * @param id The ID of the user to update.
     * @param userDto The updated user data.
     * @return ResponseEntity containing the updated user details.
     */
    @Operation(
            summary = "Update a user",
            description = "Updates an existing user using the ID provided in the URL.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "User updated successfully",
                content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Validation error or invalid JSON",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "User ID", example = "1", required = true) @PathVariable("id")
                    Integer id,
            @Validated({Default.class, ValidationGroups.Update.class}) @RequestBody
                    UserDto userDto) {
        UserDto updated = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a user by its ID.
     *
     * @param id The ID of the user to delete.
     */
    @Operation(summary = "Delete a user", description = "Deletes a user by its ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", example = "1", required = true) @PathVariable("id")
                    Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint to add a role to a user.
     *
     * @param id The ID of the user.
     * @param roleName The name of the role to add.
     * @return ResponseEntity containing the updated UserDto.
     */
    @Operation(
            summary = "Add a role to a user",
            description = "Adds a single role to the specified user using the role name.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "User role added successfully",
                content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(
                responseCode = "404",
                description = "User or Role not found",
                content = @Content)
    })
    @PostMapping(value = "/{id}/roles/{roleName}")
    public ResponseEntity<UserDto> addRoleToUser(
            @Parameter(description = "User ID", example = "1", required = true) @PathVariable("id")
                    Integer id,
            @Parameter(description = "Role name", example = "ROLE_USER", required = true)
                    @PathVariable("roleName")
                    String roleName) {
        UserDto updated = userService.addRoleToUser(id, roleName);
        return ResponseEntity.ok(updated);
    }

    /**
     * Endpoint to update the entire set of roles for a user.
     *
     * @param id The ID of the user.
     * @param roleNames The list of role names to assign to the user.
     * @return ResponseEntity containing the updated UserDto.
     */
    @Operation(
            summary = "Update user roles",
            description = "Replaces the entire set of roles for the specified user.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "User roles updated successfully",
                content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(
                responseCode = "404",
                description = "User or Role not found",
                content = @Content)
    })
    @PutMapping(value = "/{id}/roles", consumes = "application/json")
    public ResponseEntity<UserDto> updateUserRoles(
            @Parameter(description = "User ID", example = "1", required = true) @PathVariable("id")
                    Integer id,
            @RequestBody List<String> roleNames) {
        UserDto updated = userService.updateUserRoles(id, roleNames);
        return ResponseEntity.ok(updated);
    }
}
