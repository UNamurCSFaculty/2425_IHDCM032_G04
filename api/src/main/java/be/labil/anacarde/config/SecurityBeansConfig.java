package be.labil.anacarde.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
/**
 * This class provides the necessary security-related bean definitions for the application. It
 * includes the definition of a PasswordEncoder bean that is used for encoding passwords using the
 * BCrypt algorithm.
 */
public class SecurityBeansConfig {

    /**
     * This method instantiates and returns a BCryptPasswordEncoder which is used for encoding user
     * passwords.
     *
     * @return A PasswordEncoder instance based on the BCrypt algorithm.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
