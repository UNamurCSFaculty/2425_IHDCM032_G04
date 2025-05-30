package be.labil.anacarde.application.exception;

/**
 * Ensemble de code d'erreur pouvant être renvoyé par l'API. Possède une clé i18n afin d'être
 * transmis dans la langue de l'utilisateur.
 */
public enum ApiErrorCode {
	ACCESS_DENIED("access.denied"),
	ACCESS_DISABLED("access.disabled"),
	ACCESS_DISABLED_VALIDATION("access.disabled.validation"),
	ACCESS_FORBIDDEN("access.forbidden"),
	ACCESS_FORBIDDEN_CSRF("access.forbidden.csrf"),
	ACCESS_FORBIDDEN_CSRF_MISSING("access.forbidden.csrf.missing"),
	ACCESS_UNAUTHORIZED("access.unauthorized"),
	BAD_REQUEST("bad_request"),
	CONFLICT("conflict"),
	CONFLICT_EMAIL_EXISTS("email.exists"),
	CONFLICT_PHONE_EXISTS("phone.exists"),
	CONFLICT_AGRICULTURAL_ID_EXISTS("agriculture_id.exists"),
	DEFAULT_ERROR("default.error"),
	INTERNAL_ERROR("internal_error"),
	MESSAGE_NOT_READABLE("message.not_readable"),
	METHOD_NOT_ALLOWED("method.not_allowed"),
	MISSING_PATH_VARIABLE("missing.path_variable"),
	MISSING_REQUEST_PARAM("missing.request_param"),
	NO_HANDLER_FOUND("no_handler_found"),
	RESOURCE_NOT_FOUND("resource.not_found"),
	STALE_OBJECT("stale.object"),
	STORAGE_ERROR("storage.error"),
	VALIDATION_ERROR("validation.error");

	private final String code;
	ApiErrorCode(String code) {
		this.code = code;
	}
	public String code() {
		return code;
	}
}