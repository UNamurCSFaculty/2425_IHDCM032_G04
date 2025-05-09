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

public class ApiError {
	@JsonProperty("message")

	@JsonInclude(JsonInclude.Include.NON_ABSENT) // Exclude from JSON if absent
	@JsonSetter(nulls = Nulls.FAIL) // FAIL setting if the value is null
	private String message = null;

	@JsonProperty("errors")
	@Valid
	private List<ApiErrorErrors> errors = null;

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

	public void setErrors(List<ApiErrorErrors> errors) {
		this.errors = errors;
	}

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

	@Override
	public int hashCode() {
		return Objects.hash(message, errors);
	}

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
	 * Convert the given object to string with each line indented by 4 spaces (except the first
	 * line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}