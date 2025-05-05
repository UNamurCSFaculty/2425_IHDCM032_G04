package be.labil.anacarde.application.exception;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.NoHandlerFoundException;

public class GlobalExceptionHandlerTest {

	private final HttpServletRequest request = new MockHttpServletRequest();

	@Test
	public void testExtractErrorCode() throws Exception {
		GlobalExceptionHandler handler = new GlobalExceptionHandler(null);
		Method method = GlobalExceptionHandler.class.getDeclaredMethod("extractErrorCode", String.class);
		method.setAccessible(true);

		String errorMsg1 = "La valeur de clé en double viole la contrainte d'unicité [matricule]";
		String expected1 = "matricule";
		String result1 = (String) method.invoke(handler, errorMsg1);
		assertEquals(expected1, result1, "Le code d'erreur devrait être 'matricule'");

		String errorMsg2 = "Une erreur inattendue s'est produite";
		String expected2 = "default.error";
		String result2 = (String) method.invoke(handler, errorMsg2);
		assertEquals(expected2, result2, "Le code d'erreur devrait être 'default.error'");
	}

	@Test
	public void testHandleValidation() {
		MethodArgumentNotValidException ex = createTestValidationException("username", "doit être non nul");
		GlobalExceptionHandler handler = new GlobalExceptionHandler(null);

		ResponseEntity<ApiErrorResponse> response = handler.handleValidation(ex, request);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		ApiErrorResponse body = response.getBody();
		assertNotNull(body, "Le corps de la réponse ne doit pas être null");
		assertNotNull(body.getErrors(), "La liste d'erreurs ne doit pas être null");
		assertEquals(1, body.getErrors().size(), "Une seule erreur doit être présente");
		ErrorDetail detail = body.getErrors().get(0);
		assertEquals("username", detail.getField(), "Le champ doit être 'username'");
		assertEquals("doit être non nul", detail.getMessage(), "Le message d'erreur doit correspondre");
	}

	@Test
	public void testHandleResourceNotFound() {
		ResourceNotFoundException ex = new ResourceNotFoundException("Utilisateur non trouvé");
		GlobalExceptionHandler handler = new GlobalExceptionHandler(null);

		ResponseEntity<ApiErrorResponse> response = handler.handleNotFound(ex, request);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		ApiErrorResponse body = response.getBody();
		assertNotNull(body);
		ErrorDetail detail = body.getErrors().get(0);
		assertEquals("Utilisateur non trouvé", detail.getMessage());
	}

	@Test
	public void testHandleBadRequestException() {
		BadRequestException ex = new BadRequestException("Le rôle est déjà attribué à l'utilisateur");
		GlobalExceptionHandler handler = new GlobalExceptionHandler(null);

		ResponseEntity<ApiErrorResponse> response = handler.handleBadRequest(ex, request);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

		ApiErrorResponse body = response.getBody();
		assertNotNull(body);
		ErrorDetail detail = body.getErrors().get(0);
		assertEquals("Le rôle est déjà attribué à l'utilisateur", detail.getMessage());
	}

	@Test
	public void testHandleDataIntegrityViolation_MissingPassword() {
		StaticMessageSource messageSource = new StaticMessageSource();
		messageSource.addMessage("password", Locale.FRENCH, "Le mot de passe est obligatoire");
		LocaleContextHolder.setLocale(Locale.FRENCH);

		GlobalExceptionHandler handler = new GlobalExceptionHandler(messageSource);
		Exception rootCause = new Exception("Violation de contrainte [password]");
		DataIntegrityViolationException ex = new DataIntegrityViolationException("Erreur", rootCause);

		ResponseEntity<ApiErrorResponse> response = handler.handleConflict(ex, request);
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		ApiErrorResponse body = response.getBody();
		assertNotNull(body);
		ErrorDetail detail = body.getErrors().get(0);
		assertEquals("Le mot de passe est obligatoire", detail.getMessage());
	}

	@Test
	public void testHandleDataIntegrityViolation_DefaultError() {
		StaticMessageSource messageSource = new StaticMessageSource();
		LocaleContextHolder.setLocale(Locale.FRENCH);

		GlobalExceptionHandler handler = new GlobalExceptionHandler(messageSource);
		Exception rootCause = new Exception("Une autre erreur sans contrainte");
		DataIntegrityViolationException ex = new DataIntegrityViolationException("Erreur", rootCause);

		ResponseEntity<ApiErrorResponse> response = handler.handleConflict(ex, request);
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		ApiErrorResponse body = response.getBody();
		assertNotNull(body);
		ErrorDetail detail = body.getErrors().get(0);
		assertEquals("Erreur d'intégrité des données", detail.getMessage());
	}

	@Test
	public void testHandleHttpMessageNotReadable_WithJsonParseException() {
		HttpMessageNotReadableException ex = createHttpMessageNotReadableExceptionWithJsonParseException();
		GlobalExceptionHandler handler = new GlobalExceptionHandler(null);

		ResponseEntity<ApiErrorResponse> response = handler.handleUnreadable(ex, request);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		ApiErrorResponse body = response.getBody();
		assertNotNull(body);
		ErrorDetail detail = body.getErrors().get(0);
		assertTrue(detail.getMessage().contains("Syntaxe JSON invalide"), "Doit indiquer une syntaxe JSON invalide");
	}

	@Test
	public void testHandleHttpMessageNotReadable_InvalidTypeIdException() {
		InvalidTypeIdException iti = new InvalidTypeIdException(null, "missing type id property 'type'", null, "type");
		HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Erreur", iti,
				createDummyHttpInputMessage());
		GlobalExceptionHandler handler = new GlobalExceptionHandler(null);

		ResponseEntity<ApiErrorResponse> response = handler.handleUnreadable(ex, request);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		ApiErrorResponse body = response.getBody();
		assertNotNull(body);
		ErrorDetail detail = body.getErrors().getFirst();
		assertEquals("Le champ discriminant 'type' est obligatoire.", detail.getMessage());
	}

	@Test
	public void testHandleHttpMessageNotReadable_Other() {
		HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Erreur",
				createDummyHttpInputMessage());
		GlobalExceptionHandler handler = new GlobalExceptionHandler(null);

		ResponseEntity<ApiErrorResponse> response = handler.handleUnreadable(ex, request);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		ApiErrorResponse body = response.getBody();
		assertNotNull(body);
		ErrorDetail detail = body.getErrors().getFirst();
		assertEquals("Message HTTP illisible.", detail.getMessage());
	}

	@Test
	public void testHandleMethodNotAllowed() {
		HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("PUT",
				List.of("GET", "POST"));
		GlobalExceptionHandler handler = new GlobalExceptionHandler(null);

		ResponseEntity<ApiErrorResponse> response = handler.handleMethodNotAllowed(ex, request);
		assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
		ApiErrorResponse body = response.getBody();
		assertNotNull(body);
		ErrorDetail detail = body.getErrors().getFirst();
		assertTrue(detail.getMessage().contains("PUT"));
		assertTrue(detail.getMessage().contains("GET, POST"));
	}

	@Test
	public void testHandleAuthenticationException() {
		AuthenticationException ex = new AuthenticationException("Échec de login") {
		};
		GlobalExceptionHandler handler = new GlobalExceptionHandler(null);

		ResponseEntity<ApiErrorResponse> response = handler.handleAuthenticationException(ex, request);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		ApiErrorResponse body = response.getBody();
		assertNotNull(body);
		ErrorDetail detail = body.getErrors().getFirst();
		assertTrue(detail.getMessage().contains("Échec de l'authentification"));
	}

	@Test
	public void testHandleAccessDenied() {
		AccessDeniedException ex = new AccessDeniedException("Accès refusé");
		GlobalExceptionHandler handler = new GlobalExceptionHandler(null);

		ResponseEntity<ApiErrorResponse> response = handler.handleAccessDenied(ex, request);
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
		ApiErrorResponse body = response.getBody();
		assertNotNull(body);
		ErrorDetail detail = body.getErrors().getFirst();
		assertTrue(detail.getMessage().contains("Accès refusé"));
	}

	@Test
	public void testHandleStaleObjectStateException() {
		StaleObjectStateException ex = new StaleObjectStateException("Test", null);
		GlobalExceptionHandler handler = new GlobalExceptionHandler(null);

		ResponseEntity<ApiErrorResponse> response = handler.handleStale(ex, request);
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		ApiErrorResponse body = response.getBody();
		assertNotNull(body);
		ErrorDetail detail = body.getErrors().getFirst();
		assertTrue(detail.getMessage().contains("modifiée par un autre utilisateur"));
	}

	@Test
	public void testHandleGenericExceptions() {
		Exception ex = new Exception("Erreur générique");
		GlobalExceptionHandler handler = new GlobalExceptionHandler(null);

		ResponseEntity<ApiErrorResponse> response = handler.handleGeneric(ex, request);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		ApiErrorResponse body = response.getBody();
		assertNotNull(body);
		ErrorDetail detail = body.getErrors().getFirst();
		assertTrue(detail.getMessage().contains("Une erreur interne s'est produite"));
	}

	@Test
	public void testHandleNoHandlerFoundException() {
		HttpHeaders headers = new HttpHeaders();
		NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/inexistant", headers);
		GlobalExceptionHandler handler = new GlobalExceptionHandler(null);

		ResponseEntity<ApiErrorResponse> response = handler.handleNoHandlerFound(ex, request);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		ApiErrorResponse body = response.getBody();
		assertNotNull(body);
		ErrorDetail detail = body.getErrors().getFirst();
		assertTrue(detail.getMessage().contains("/inexistant"));
	}

	@Test
	public void testHandleMissingPathVariableException() throws Exception {
		MethodParameter param = obtainTestMethodParameter();
		MissingPathVariableException ex = new MissingPathVariableException("id", param);
		GlobalExceptionHandler handler = new GlobalExceptionHandler(null);

		ResponseEntity<ApiErrorResponse> response = handler.handleMissingPathVariable(ex, request);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		ApiErrorResponse body = response.getBody();
		assertNotNull(body);
		ErrorDetail detail = body.getErrors().getFirst();
		assertTrue(detail.getMessage().contains("id"));
	}

	@Test
	public void testHandleMissingServletRequestParameterException() {
		MissingServletRequestParameterException ex = new MissingServletRequestParameterException("name", "String");
		GlobalExceptionHandler handler = new GlobalExceptionHandler(null);

		ResponseEntity<ApiErrorResponse> response = handler.handleMissingServletParam(ex, request);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		ApiErrorResponse body = response.getBody();
		assertNotNull(body);
		ErrorDetail detail = body.getErrors().getFirst();
		assertTrue(detail.getMessage().contains("name"));
	}

	@Test
	public void testHandleApiError_singleDetail() {
		// Préparer une requête factice avec URI
		MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/test");
		GlobalExceptionHandler handler = new GlobalExceptionHandler(null);

		// Construire l'exception métier avec un seul détail
		ApiErrorException ex = new ApiErrorException(HttpStatus.BAD_REQUEST, "custom.code", "fieldName",
				"detail message");

		// Appeler le handler
		ResponseEntity<ApiErrorResponse> response = handler.handleApiError(ex, req);

		// Vérifications
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		ApiErrorResponse body = response.getBody();
		assertNotNull(body);
		assertEquals(400, body.getStatus());
		assertEquals("/api/test", body.getPath());
		assertEquals("custom.code", body.getCode());

		List<ErrorDetail> errors = body.getErrors();
		assertEquals(1, errors.size(), "Une seule erreur doit être présente");

		ErrorDetail detail = errors.get(0);
		assertEquals("fieldName", detail.getField());
		assertEquals("custom.code", detail.getCode());
		assertEquals("detail message", detail.getMessage());
	}

	@Test
	public void testHandleApiError_multipleDetails() {
		MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/multi");
		GlobalExceptionHandler handler = new GlobalExceptionHandler(null);

		// Construire plusieurs détails d'erreur
		List<ErrorDetail> list = List.of(new ErrorDetail("f1", "code1", "message1"),
				new ErrorDetail("f2", "code2", "message2"));
		ApiErrorException ex = new ApiErrorException(HttpStatus.CONFLICT, "global.code", list);

		ResponseEntity<ApiErrorResponse> response = handler.handleApiError(ex, req);

		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		ApiErrorResponse body = response.getBody();
		assertNotNull(body);
		assertEquals(409, body.getStatus());
		assertEquals("/api/multi", body.getPath());
		assertEquals("global.code", body.getCode());

		List<ErrorDetail> errors = body.getErrors();
		assertEquals(2, errors.size(), "Deux erreurs doivent être présentes");

		ErrorDetail d1 = errors.get(0);
		assertEquals("f1", d1.getField());
		assertEquals("code1", d1.getCode());
		assertEquals("message1", d1.getMessage());

		ErrorDetail d2 = errors.get(1);
		assertEquals("f2", d2.getField());
		assertEquals("code2", d2.getCode());
		assertEquals("message2", d2.getMessage());
	}

	// Helpers

	private static HttpInputMessage createDummyHttpInputMessage() {
		return new HttpInputMessage() {
			@Override
			public org.springframework.http.HttpHeaders getHeaders() {
				return new org.springframework.http.HttpHeaders();
			}

			@Override
			public InputStream getBody() {
				return new ByteArrayInputStream(new byte[0]);
			}
		};
	}

	private static HttpMessageNotReadableException createHttpMessageNotReadableExceptionWithJsonParseException() {
		JsonParseException jpe = new JsonParseException(null, "Original parse error");
		return new HttpMessageNotReadableException("Erreur", jpe, createDummyHttpInputMessage());
	}

	private MethodArgumentNotValidException createTestValidationException(String field, String errorMessage) {
		Object target = new Object();
		BindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
		bindingResult.addError(new FieldError("target", field, errorMessage));
		return new MethodArgumentNotValidException(null, bindingResult);
	}

	private MethodParameter obtainTestMethodParameter() throws NoSuchMethodException {
		Method method = this.getClass().getDeclaredMethod("helperMethodForParameter", String.class);
		return new MethodParameter(method, 0);
	}

	private void helperMethodForParameter(String dummy) {
		// Méthode de support pour obtenir un MethodParameter dans les tests
	}
}
