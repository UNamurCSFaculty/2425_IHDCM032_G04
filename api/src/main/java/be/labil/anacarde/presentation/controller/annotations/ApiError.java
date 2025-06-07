package be.labil.anacarde.presentation.controller.annotations;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Représente le format standardisé d’une erreur renvoyée par l’API REST.
 * <p>
 * Contient un message principal et une liste de détails d’erreurs supplémentaires.
 */
public class ApiError {
	@JsonProperty("message")
	@JsonInclude(JsonInclude.Include.NON_ABSENT) // Exclude from JSON if absent
	@JsonSetter(nulls = Nulls.FAIL) // FAIL setting if the value is null
	private String message = null;

	@JsonProperty("errors")
	@Valid
	private List<ApiErrorErrors> errors = null;

	/**
	 * Définit le message d’erreur principal.
	 *
	 * @param message le message human-readable décrivant l’erreur
	 * @return l’instance courante pour enchaînement
	 */
	public ApiError message(String message) {

		this.message = message;
		return this;
	}

	/**
	 * human-readable error message
	 * 
	 * @return message
	 **/
	@Schema(description = "human-readable error message")
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ApiError errors(List<ApiErrorErrors> errors) {

		this.errors = errors;
		return this;
	}

	/**
	 * Définit la liste complète des erreurs détaillées.
	 *
	 * @param errors liste d’objets {@link ApiErrorErrors}
	 * @return l’instance courante pour enchaînement
	 */
	public ApiError addErrorsItem(ApiErrorErrors errorsItem) {
		if (this.errors == null) {
			this.errors = new ArrayList<ApiErrorErrors>();
		}
		this.errors.add(errorsItem);
		return this;
	}

	/**
	 * Get errors
	 * 
	 * @return errors
	 **/
	@Schema(description = "")
	@Valid
	public List<ApiErrorErrors> getErrors() {
		return errors;
	}

	/**
	 * Modifie la liste des erreurs détaillées.
	 *
	 * @param errors nouvelle liste d’objets {@link ApiErrorErrors}
	 */
	public void setErrors(List<ApiErrorErrors> errors) {
		this.errors = errors;
	}

	/**
	 * Compare cette instance à un autre objet pour l’égalité.
	 *
	 * @param o l’objet à comparer
	 * @return {@code true} si les messages et listes d’erreurs sont identiques
	 */
	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ApiError apiError = (ApiError) o;
		return Objects.equals(this.message, apiError.message)
				&& Objects.equals(this.errors, apiError.errors);
	}

	/**
	 * Calcule le hash code de cette instance.
	 *
	 * @return code de hachage basé sur les champs message et errors
	 */
	@Override
	public int hashCode() {
		return Objects.hash(message, errors);
	}

	/**
	 * Génère une représentation textuelle indentée de cette erreur.
	 *
	 * @return chaîne de caractères formatée
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class ApiError {\n");

		sb.append("    message: ").append(toIndentedString(message)).append("\n");
		sb.append("    errors: ").append(toIndentedString(errors)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Indente chaque ligne de la chaîne fournie de 4 espaces (sauf la première).
	 *
	 * @param o objet à convertir en chaîne
	 * @return chaîne indentée
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}