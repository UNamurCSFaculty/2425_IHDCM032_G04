package be.labil.anacarde.application.exception;

public enum ApiErrorCode {
	VALIDATION_ERROR("validation.error"), RESOURCE_NOT_FOUND("resource.not_found"), BAD_REQUEST(
			"bad_request"), DEFAULT_ERROR("default.error"), MESSAGE_NOT_READABLE(
					"message.not_readable"), METHOD_NOT_ALLOWED("method.not_allowed"), STALE_OBJECT(
							"stale.object"), NO_HANDLER_FOUND("no_handler_found"), MISSING_PATH_VARIABLE(
									"missing.path_variable"), MISSING_REQUEST_PARAM(
											"missing.request_param"), ACCESS_UNAUTHORIZED(
													"access.unauthorized"), ACCESS_DENIED(
															"access.denied"), INTERNAL_ERROR("internal_error");

	private final String code;
	ApiErrorCode(String code) {
		this.code = code;
	}
	public String code() {
		return code;
	}
}