package be.labil.anacarde.application.exception;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class DocumentStorageException extends ApiErrorException {
	public DocumentStorageException(String msg, Throwable cause) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, ApiErrorCode.STORAGE_ERROR.code(),
				List.of(new ErrorDetail(null, ApiErrorCode.STORAGE_ERROR.code(), msg)));
		log.error(msg, cause);
	}
}