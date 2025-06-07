package be.labil.anacarde.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation de validation pour renforcer la sécurité des mots de passe.
 * <p>
 * Le mot de passe doit contenir au minimum :
 * <ul>
 *   <li>Une lettre majuscule</li>
 *   <li>Une lettre minuscule</li>
 *   <li>Un chiffre</li>
 *   <li>Un caractère spécial</li>
 * </ul>
 * <p>
 * Les messages par défaut peuvent être personnalisés par critère.
 */
@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {

	String message() default "Le mot de passe ne respecte pas les critères de complexité.";
	String messageUppercase() default "Le mot de passe doit contenir au moins une majuscule.";
	String messageLowercase() default "Le mot de passe doit contenir au moins une minuscule.";
	String messageDigit() default "Le mot de passe doit contenir au moins un chiffre.";
	String messageSpecialChar() default "Le mot de passe doit contenir au moins un caractère spécial.";

	/**
	 * Groupes de validation (facultatif).
	 */
	Class<?>[] groups() default {};

	/**
	 * Payload associé à la violation (facultatif), permet de fournir des métadonnées.
	 */
	Class<? extends Payload>[] payload() default {};
}