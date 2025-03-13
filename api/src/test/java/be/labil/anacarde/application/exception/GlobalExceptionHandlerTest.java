package be.labil.anacarde.application.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

/** Test de la classe GlobalExceptionHandler, en particulier de la méthode extractErrorCode. */
public class GlobalExceptionHandlerTest {

    /**
     * Teste la méthode extractErrorCode de GlobalExceptionHandler.
     *
     * @throws Exception en cas d'erreur lors de l'invocation par réflexion.
     */
    @Test
    public void testExtractErrorCode() throws Exception {
        GlobalExceptionHandler handler = new GlobalExceptionHandler(null);

        Method method =
                GlobalExceptionHandler.class.getDeclaredMethod("extractErrorCode", String.class);
        method.setAccessible(true);

        // Cas 1 : Extraction du code d'erreur à partir du message
        String errorMsg1 = "La valeur de clé en double viole la contrainte d'unicité [matricule]";
        String expected1 = "matricule";
        String result1 = (String) method.invoke(handler, errorMsg1);
        assertEquals(expected1, result1, "Le code d'erreur devrait être 'matricule'");

        // Cas 2 : Retour du code d'erreur par défaut lorsque le code n'est pas trouvé
        String errorMsg3 = "Une erreur inattendue s'est produite";
        String expected3 = "default.error";
        String result3 = (String) method.invoke(handler, errorMsg3);
        assertEquals(expected3, result3, "Le code d'erreur devrait être 'default.error'");
    }
}
