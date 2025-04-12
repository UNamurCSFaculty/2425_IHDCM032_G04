package be.labil.anacarde.presentation.controller.annotations;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.lang.annotation.*;

@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Positive(message = "L'identifiant doit être un entier positif")
@NotNull(message = "L'identifiant est obligatoire")
@Parameter(description = "Identifiant de ressource", example = "1", required = true)
public @interface ApiValidId { }