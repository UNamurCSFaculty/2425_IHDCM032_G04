package be.labil.anacarde.application.exception;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalExceptionHandlerTest {

    @Test
    public void testExtractErrorCode() throws Exception {
        GlobalExceptionHandler handler = new GlobalExceptionHandler(null);

        Method method = GlobalExceptionHandler.class.getDeclaredMethod("extractErrorCode", String.class);
        method.setAccessible(true);

        // Case 1 : extract the error code from the error message
        String errorMsg1 = "Duplicate key value violates unique constraint [matricule]";
        String expected1 = "matricule";
        String result1 = (String) method.invoke(handler, errorMsg1);
        assertEquals(expected1, result1, "The error code should be 'matricule'");

        // Case 2 : return the default error code if the error code is not found
        String errorMsg3 = "Some unexpected error occurred";
        String expected3 = "default.error";
        String result3 = (String) method.invoke(handler, errorMsg3);
        assertEquals(expected3, result3, "The error code should be 'default.error'");
    }
}
