package be.labil.anacarde.application.exception;

public enum ApiErrorCode {
	ACCESS_DENIED("access.denied"),
	ACCESS_UNAUTHORIZED("access.unauthorized"),
	BAD_REQUEST("bad_request"),
	CONFLICT("conflict"),
	DEFAULT_ERROR("default.error"),
	INTERNAL_ERROR("internal_error"),
	MESSAGE_NOT_READABLE("message.not_readable"),
	METHOD_NOT_ALLOWED("method.not_allowed"),
	MISSING_PATH_VARIABLE("missing.path_variable"),
	MISSING_REQUEST_PARAM("missing.request_param"),
	NO_HANDLER_FOUND("no_handler_found"),
	CONFLICT_EMAIL_EXISTS("email.exists"),
	CONFLICT_PHONE_EXISTS("phone.exists"),
	CONFLICT_AGRICULTURE_ID_EXISTS("agriculture_id.exists"),
	RESOURCE_NOT_FOUND("resource.not_found"),
	STALE_OBJECT("stale.object"),
	VALIDATION_ERROR("validation.error");

	private final String code;
	ApiErrorCode(String code) {
		this.code = code;
	}
	public String code() {
		return code;
	}
}