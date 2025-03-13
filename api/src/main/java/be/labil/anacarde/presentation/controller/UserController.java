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

/**
 * Ce contrôleur fournit des points d'accès pour récupérer, créer, mettre à jour et supprimer des
 * utilisateurs.
 */
@Tag(name = "Users", description = "API de gestion des utilisateurs")
@RestController
@RequestMapping(value = "/api/users", produces = "application/json")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Récupère les informations détaillées d'un utilisateur à partir de son identifiant.
     *
     * @param id L'identifiant de l'utilisateur.
     * @return Une ResponseEntity contenant les détails de l'utilisateur.
     */
    @Operation(
            summary = "Récupérer les détails d'un utilisateur",
            description =
                    "Renvoie les informations détaillées d'un utilisateur à partir de son ID.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Utilisateur trouvé",
                content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Utilisateur non trouvé",
                content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(
            @Parameter(description = "Identifiant de l'utilisateur", example = "1", required = true)
                    @PathVariable("id")
                    Integer id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Crée un nouvel utilisateur en utilisant le groupe de validation "Create", qui rend le champ
     * mot de passe obligatoire.
     *
     * @param userDto Les données de l'utilisateur à créer.
     * @return Une ResponseEntity contenant les détails de l'utilisateur créé.
     */
    @Operation(
            summary = "Créer un utilisateur",
            description = "Crée un nouvel utilisateur dans le système.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Utilisateur créé avec succès",
                content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Erreur de validation ou JSON invalide",
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
     * Récupère la liste de tous les utilisateurs.
     *
     * @return Une ResponseEntity contenant la liste de tous les utilisateurs.
     */
    @Operation(
            summary = "Lister tous les utilisateurs",
            description = "Renvoie la liste de tous les utilisateurs présents dans le système.")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<UserDto>> listUsers() {
        List<UserDto> users = userService.listUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Met à jour un utilisateur existant en utilisant le groupe de validation "Update", qui rend le
     * champ mot de passe optionnel.
     *
     * @param id L'identifiant de l'utilisateur à mettre à jour.
     * @param userDto Les nouvelles données de l'utilisateur.
     * @return Une ResponseEntity contenant les détails de l'utilisateur mis à jour.
     */
    @Operation(
            summary = "Mettre à jour un utilisateur",
            description =
                    "Met à jour un utilisateur existant en utilisant l'ID spécifié dans l'URL.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Utilisateur mis à jour avec succès",
                content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Erreur de validation ou JSON invalide",
                content = @Content),
        @ApiResponse(
                responseCode = "404",
                description = "Utilisateur non trouvé",
                content = @Content)
    })
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "Identifiant de l'utilisateur", example = "1", required = true)
                    @PathVariable("id")
                    Integer id,
            @Validated({Default.class, ValidationGroups.Update.class}) @RequestBody
                    UserDto userDto) {
        UserDto updated = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Supprime un utilisateur à partir de son identifiant.
     *
     * @param id L'identifiant de l'utilisateur à supprimer.
     */
    @Operation(
            summary = "Supprimer un utilisateur",
            description = "Supprime un utilisateur en fonction de son identifiant.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Utilisateur supprimé avec succès"),
        @ApiResponse(
                responseCode = "404",
                description = "Utilisateur non trouvé",
                content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "Identifiant de l'utilisateur", example = "1", required = true)
                    @PathVariable("id")
                    Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Ajoute un rôle à un utilisateur.
     *
     * @param id L'identifiant de l'utilisateur.
     * @param roleName Le nom du rôle à ajouter.
     * @return Une ResponseEntity contenant l'utilisateur mis à jour.
     */
    @Operation(
            summary = "Ajouter un rôle à un utilisateur",
            description = "Ajoute un rôle spécifique à l'utilisateur en utilisant le nom du rôle.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Rôle ajouté avec succès",
                content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Utilisateur ou rôle non trouvé",
                content = @Content)
    })
    @PostMapping(value = "/{id}/roles/{roleName}")
    public ResponseEntity<UserDto> addRoleToUser(
            @Parameter(description = "Identifiant de l'utilisateur", example = "1", required = true)
                    @PathVariable("id")
                    Integer id,
            @Parameter(description = "Nom du rôle", example = "ROLE_USER", required = true)
                    @PathVariable("roleName")
                    String roleName) {
        UserDto updated = userService.addRoleToUser(id, roleName);
        return ResponseEntity.ok(updated);
    }

    /**
     * Met à jour l'ensemble des rôles d'un utilisateur.
     *
     * @param id L'identifiant de l'utilisateur.
     * @param roleNames La liste des noms de rôle à attribuer à l'utilisateur.
     * @return Une ResponseEntity contenant l'utilisateur mis à jour.
     */
    @Operation(
            summary = "Mettre à jour les rôles d'un utilisateur",
            description =
                    "Remplace l'ensemble des rôles de l'utilisateur spécifié par la liste fournie.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Rôles mis à jour avec succès",
                content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Utilisateur ou rôle non trouvé",
                content = @Content)
    })
    @PutMapping(value = "/{id}/roles", consumes = "application/json")
    public ResponseEntity<UserDto> updateUserRoles(
            @Parameter(description = "Identifiant de l'utilisateur", example = "1", required = true)
                    @PathVariable("id")
                    Integer id,
            @RequestBody List<String> roleNames) {
        UserDto updated = userService.updateUserRoles(id, roleNames);
        return ResponseEntity.ok(updated);
    }
}
