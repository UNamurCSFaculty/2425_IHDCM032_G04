package be.labil.anacarde.presentation.controller.annotations;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

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

	public void setPath(String path) {
		this.path = path;
	}

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

	public void setMessage(String message) {
		this.message = message;
	}

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
		return Objects.equals(this.path, apiErrorErrors.path) && Objects.equals(this.message, apiErrorErrors.message)
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
	 * Convert the given object to string with each line indented by 4 spaces (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}