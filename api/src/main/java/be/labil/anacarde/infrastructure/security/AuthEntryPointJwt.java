package be.labil.anacarde.infrastructure.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @brief Entry point for handling unauthorized requests in JWT authentication.
 *
 * This component is responsible for sending a 401 Unauthorized error response when an
 * authentication failure occurs.
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    /**
     * @brief Handles unauthorized access by sending a 401 error response.
     *
     * This method is invoked when an authentication exception is thrown, indicating that
     * the user is not authorized to access the requested resource. It sends a 401 Unauthorized
     * error along with an error message.
     *
     * @param request The HttpServletRequest in which the authentication attempt was made.
     * @param response The HttpServletResponse used to send the error response.
     * @param authException The exception that triggered this authentication failure.
     * @throws IOException If an input or output error occurs while handling the response.
     * @throws ServletException If a servlet-specific error occurs during processing.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // 401 Unauthorized
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }
}
