package be.labil.anacarde.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation pour valider la complexité d'un mot de passe. Le mot de passe doit contenir au moins
 * une majuscule, une minuscule, un chiffre et un caractère spécial. Les messages d'erreur peuvent
 * être personnalisés pour chaque règle.
 */
@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
	// Message par défaut si aucun des messages spécifiques n'est utilisé (ne sera pas utilisé avec
	// la logique ci-dessus)
	String message() default "Le mot de passe ne respecte pas les critères de complexité.";

	// Messages spécifiques pour chaque règle
	String messageUppercase() default "Le mot de passe doit contenir au moins une majuscule.";
	String messageLowercase() default "Le mot de passe doit contenir au moins une minuscule.";
	String messageDigit() default "Le mot de passe doit contenir au moins un chiffre.";
	String messageSpecialChar() default "Le mot de passe doit contenir au moins un caractère spécial.";

	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}