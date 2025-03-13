package be.labil.anacarde.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@AllArgsConstructor
/**
 * Authentication token filter for processing JWTs.
 *
 * <p>This filter intercepts incoming HTTP requests to extract and validate JWT tokens from cookies.
 * Upon successful validation, it sets the authentication in the SecurityContext.
 */
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    /**
     * Processes the HTTP request to authenticate a user based on the JWT token present in cookies.
     *
     * <p>This method extracts the JWT from the request, validates it, and if valid, sets the
     * authentication in the SecurityContext so that the user is considered authenticated for the
     * current request.
     *
     * @param request The HttpServletRequest being processed.
     * @param response The HttpServletResponse associated with the request.
     * @param filterChain The FilterChain to pass the request and response to the next filter.
     * @throws ServletException if a servlet error occurs during processing.
     * @throws IOException if an I/O error occurs during processing.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null) {
                String username = jwtUtil.extractUsername(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * This method iterates through the cookies in the request and returns the value of the cookie
     * named "jwt". If no such cookie is found, it returns null.
     *
     * @param request The HttpServletRequest from which to extract the JWT token.
     * @return The JWT token as a String if present; otherwise, null.
     */
    private String parseJwt(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
