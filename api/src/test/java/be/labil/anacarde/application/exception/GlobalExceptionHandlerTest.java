package be.labil.anacarde.application.exception;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonParseException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Locale;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.NoHandlerFoundException;

public class GlobalExceptionHandlerTest {

    @Test
    public void testExtractErrorCode() throws Exception {
        GlobalExceptionHandler handler = new GlobalExceptionHandler(null);
        Method method =
                GlobalExceptionHandler.class.getDeclaredMethod("extractErrorCode", String.class);
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
    public void testHandleValidationExceptions() {
        MethodArgumentNotValidException ex =
                createTestValidationException("username", "doit être non nul");
        GlobalExceptionHandler handler = new GlobalExceptionHandler(null);
        ResponseEntity<ValidationErrorResponse> response = handler.handleValidationExceptions(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ValidationErrorResponse body = response.getBody();
        assertNotNull(body, "Le corps de la réponse ne doit pas être null");
        assertTrue(
                body.getErrors().containsKey("username"),
                "L'erreur pour 'username' doit être présente");
    }

    @Test
    public void testHandleResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Utilisateur non trouvé");
        GlobalExceptionHandler handler = new GlobalExceptionHandler(null);
        ResponseEntity<ErrorResponse> response = handler.handleResourceNotFound(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Utilisateur non trouvé", body.getError());
    }

    @Test
    public void testHandleDataIntegrityViolation_MissingPassword() {
        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("password", Locale.FRENCH, "Le mot de passe est obligatoire");
        LocaleContextHolder.setLocale(Locale.FRENCH);

        GlobalExceptionHandler handler = new GlobalExceptionHandler(messageSource);
        Exception rootCause = new Exception("Violation de contrainte [password]");
        org.springframework.dao.DataIntegrityViolationException ex =
                new org.springframework.dao.DataIntegrityViolationException("Erreur", rootCause);

        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolation(ex);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Le mot de passe est obligatoire", body.getError());
    }

    @Test
    public void testHandleHttpMessageNotReadable_WithJsonParseException() {
        HttpMessageNotReadableException ex =
                createHttpMessageNotReadableExceptionWithJsonParseException();
        GlobalExceptionHandler handler = new GlobalExceptionHandler(null);
        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadable(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertTrue(body.getError().contains("Erreur de syntaxe JSON"));
    }

    @Test
    public void testHandleHttpMessageNotReadable_WithoutJsonParseException() {
        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("Erreur", createDummyHttpInputMessage());
        GlobalExceptionHandler handler = new GlobalExceptionHandler(null);
        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadable(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Message HTTP illisible.", body.getError());
    }

    @Test
    public void testHandleStaleObjectStateException() {
        StaleObjectStateException ex = new StaleObjectStateException("Test", null);
        GlobalExceptionHandler handler = new GlobalExceptionHandler(null);
        ResponseEntity<ErrorResponse> response = handler.handleStaleObjectStateException(ex);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertTrue(body.getError().contains("modifiée par un autre utilisateur"));
    }

    @Test
    public void testHandleGenericExceptions() {
        Exception ex = new Exception("Erreur générique");
        GlobalExceptionHandler handler = new GlobalExceptionHandler(null);
        ResponseEntity<ErrorResponse> response = handler.handleGenericExceptions(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertTrue(body.getError().contains("Une erreur interne s'est produite"));
    }

    @Test
    public void testHandleNoHandlerFoundException() {
        HttpHeaders headers = new HttpHeaders();
        NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/inexistant", headers);
        GlobalExceptionHandler handler = new GlobalExceptionHandler(null);
        ResponseEntity<ErrorResponse> response = handler.handleNoHandlerFoundException(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertTrue(body.getError().contains("/inexistant"));
    }

    @Test
    public void testHandleMissingPathVariableException() throws Exception {
        MethodParameter param = obtainTestMethodParameter();
        MissingPathVariableException ex = new MissingPathVariableException("id", param);
        GlobalExceptionHandler handler = new GlobalExceptionHandler(null);
        ResponseEntity<ErrorResponse> response =
                handler.handleMissingPathVariableException(null, ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertTrue(body.getError().contains("id"));
    }

    @Test
    public void testHandleMissingServletRequestParameterException() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("name", "String");
        GlobalExceptionHandler handler = new GlobalExceptionHandler(null);
        ResponseEntity<ErrorResponse> response =
                handler.handleMissingServletRequestParameterException(null, ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertTrue(body.getError().contains("name"));
    }

    /**
     * Renvoie une implémentation dummy de HttpInputMessage. Cette méthode est factorisée pour être
     * utilisée dans les tests qui nécessitent de fournir un HttpInputMessage (ex. pour simuler une
     * HttpMessageNotReadableException).
     */
    private static HttpInputMessage createDummyHttpInputMessage() {
        return new HttpInputMessage() {
            @Override
            public HttpHeaders getHeaders() {
                return new HttpHeaders();
            }

            @Override
            public InputStream getBody() {
                return new ByteArrayInputStream(new byte[0]);
            }
        };
    }

    /**
     * Crée une HttpMessageNotReadableException simulée avec un JsonParseException. Utilise un
     * HttpInputMessage dummy pour satisfaire le constructeur non déprécié.
     */
    private static HttpMessageNotReadableException
            createHttpMessageNotReadableExceptionWithJsonParseException() {
        JsonParseException jpe = new JsonParseException(null, "Syntaxe JSON invalide");
        return new HttpMessageNotReadableException("Erreur", jpe, createDummyHttpInputMessage());
    }

    /**
     * Construit une MethodArgumentNotValidException simulée en créant un BindingResult contenant un
     * FieldError pour le champ spécifié. Cela permet de centraliser la création de cette exception
     * pour les tests.
     */
    private MethodArgumentNotValidException createTestValidationException(
            String field, String errorMessage) {
        Object target = new Object();
        BindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.addError(new FieldError("target", field, errorMessage));
        return new MethodArgumentNotValidException(null, bindingResult);
    }

    /**
     * Permet d'obtenir un objet MethodParameter en utilisant une méthode de support de cette
     * classe. Cela évite d'ajouter une méthode "dummy" dans le code de production et centralise la
     * logique de création du MethodParameter pour les tests.
     */
    private MethodParameter obtainTestMethodParameter() throws NoSuchMethodException {
        Method method = this.getClass().getDeclaredMethod("helperMethodForParameter", String.class);
        return new MethodParameter(method, 0);
    }

    /** Méthode de support utilisée uniquement pour obtenir un MethodParameter dans les tests. */
    private void helperMethodForParameter(String dummy) {
        // Méthode vide, uniquement pour obtenir un MethodParameter.
    }
}
