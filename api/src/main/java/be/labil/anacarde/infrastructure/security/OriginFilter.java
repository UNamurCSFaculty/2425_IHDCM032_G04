package be.labil.anacarde.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class OriginFilter extends OncePerRequestFilter {
	@Value("${app.trusted.origin}")
	private String trustedOrigin;

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws ServletException, IOException {
		String origin = req.getHeader("Origin");
		if (origin != null && !origin.equals(trustedOrigin)) {
			res.sendError(HttpStatus.FORBIDDEN.value(), "Invalid Origin");
			return;
		}
		chain.doFilter(req, res);
	}
}
