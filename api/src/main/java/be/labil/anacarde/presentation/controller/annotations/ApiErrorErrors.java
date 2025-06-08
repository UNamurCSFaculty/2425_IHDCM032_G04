package be.labil.anacarde.presentation.controller.annotations;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

/**
 * Représente un détail d’erreur individuel dans la réponse API.
 * <p>
 * Utilisé pour indiquer le chemin JSON concerné, le message d’erreur et un code d’erreur spécifique
 * par élément d’erreur.
 */
public class ApiErrorErrors {
	@JsonProperty("path")

	@JsonInclude(JsonInclude.Include.NON_ABSENT) // Exclude from JSON if absent
	@JsonSetter(nulls = Nulls.FAIL) // FAIL setting if the value is null
	private String path = null;

	@JsonProperty("message")

	@JsonInclude(JsonInclude.Include.NON_ABSENT) // Exclude from JSON if absent
	@JsonSetter(nulls = Nulls.FAIL) // FAIL setting if the value is null
	private String message = null;

	@JsonProperty("errorCode")

	@JsonInclude(JsonInclude.Include.NON_ABSENT) // Exclude from JSON if absent
	@JsonSetter(nulls = Nulls.FAIL) // FAIL setting if the value is null
	private String errorCode = null;

	/**
	 * Définit le chemin JSON associé à cette erreur.
	 *
	 * @param path
	 *            chemin JSON (ex. "user.email")
	 * @return l’instance courante pour enchaînement
	 */
	public ApiErrorErrors path(String path) {

		this.path = path;
		return this;
	}

	/**
	 * For input validation errors, identifies where in the JSON request body the error occurred.
	 * 
	 * @return path
	 **/
	@Schema(description = "For input validation errors, identifies where in the  JSON request body the error occurred. ")
	public String getPath() {
		return path;
	}

	/**
	 * Modifie le chemin JSON associé à l’erreur.
	 *
	 * @param path
	 *            nouveau chemin JSON
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Définit le message d’erreur.
	 *
	 * @param message
	 *            message human-readable décrivant l’erreur
	 * @return l’instance courante pour enchaînement
	 */
	public ApiErrorErrors message(String message) {

		this.message = message;
		return this;
	}

	/**
	 * Human-readable error message.
	 * 
	 * @return message
	 **/
	@Schema(description = "Human-readable error message.")
	public String getMessage() {
		return message;
	}

	/**
	 * Modifie le message d’erreur.
	 *
	 * @param message
	 *            nouveau message human-readable
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Définit le code d’erreur.
	 *
	 * @param errorCode
	 *            code métier ou technique de l’erreur
	 * @return l’instance courante pour enchaînement
	 */
	public ApiErrorErrors errorCode(String errorCode) {

		this.errorCode = errorCode;
		return this;
	}

	/**
	 * Code indicating error type.
	 * 
	 * @return errorCode
	 **/
	@Schema(description = "Code indicating error type.")
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Modifie le code d’erreur.
	 *
	 * @param errorCode
	 *            nouveau code métier
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ApiErrorErrors apiErrorErrors = (ApiErrorErrors) o;
		return Objects.equals(this.path, apiErrorErrors.path)
				&& Objects.equals(this.message, apiErrorErrors.message)
				&& Objects.equals(this.errorCode, apiErrorErrors.errorCode);
	}

	@Override
	public int hashCode() {
		return Objects.hash(path, message, errorCode);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class ApiErrorErrors {\n");
		sb.append("    path: ").append(toIndentedString(path)).append("\n");
		sb.append("    message: ").append(toIndentedString(message)).append("\n");
		sb.append("    errorCode: ").append(toIndentedString(errorCode)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convertit l’objet en chaîne indentée pour l’affichage.
	 *
	 * @param o
	 *            objet à convertir
	 * @return chaîne avec indentations
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}