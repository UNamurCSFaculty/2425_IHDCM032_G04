package be.labil.anacarde.infrastructure.security;

import be.labil.anacarde.domain.model.Admin;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component("authz") // nom d’accès depuis SpEL
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthzUtil {

	/** Renvoie vrai si le principal est une instance de Admin. */
	public boolean isAdmin(UserDetails principal) {
		return principal instanceof Admin;
	}
}