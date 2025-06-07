package be.labil.anacarde.domain.validation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Validateur pour les mots de passe forts. Vérifie que le mot de passe respecte les règles de
 * complexité : - Au moins une lettre majuscule - Au moins une lettre minuscule - Au moins un
 * chiffre - Au moins un caractère spécial - Longueur minimale de 8 caractères (gérée par
 * l'annotation @Size)
 */
public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

	private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
	private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
	private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
	private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^A-Za-z0-9]");

	private String messageUppercase;
	private String messageLowercase;
	private String messageDigit;
	private String messageSpecialChar;

	@Override
	public void initialize(StrongPassword constraintAnnotation) {
		messageUppercase = constraintAnnotation.messageUppercase();
		messageLowercase = constraintAnnotation.messageLowercase();
		messageDigit = constraintAnnotation.messageDigit();
		messageSpecialChar = constraintAnnotation.messageSpecialChar();
	}

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		if (password == null || password.isEmpty()) {
			return true;
		}

		context.disableDefaultConstraintViolation();

		if (!UPPERCASE_PATTERN.matcher(password).find()) {
			context.buildConstraintViolationWithTemplate(messageUppercase).addConstraintViolation();
			return false;
		}

		if (!LOWERCASE_PATTERN.matcher(password).find()) {
			context.buildConstraintViolationWithTemplate(messageLowercase).addConstraintViolation();
			return false;
		}

		if (!DIGIT_PATTERN.matcher(password).find()) {
			context.buildConstraintViolationWithTemplate(messageDigit).addConstraintViolation();
			return false;
		}

		if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
			context.buildConstraintViolationWithTemplate(messageSpecialChar)
					.addConstraintViolation();
			return false;
		}

		return true;
	}
}