package be.labil.anacarde.config;

import be.labil.anacarde.infrastructure.security.AuthEntryPointJwt;
import be.labil.anacarde.infrastructure.security.AuthTokenFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@AllArgsConstructor
@EnableMethodSecurity
/**
 * @brief Security configuration for the application.
 *     <p>This class configures the security settings for the application. It defines beans for
 *     authentication providers, authentication managers, and the security filter chain. It also
 *     sets up HTTP security policies such as disabling CORS and CSRF, setting session management to
 *     stateless, and configuring request authorization.
 */
public class SecurityConfiguration {

    private final AuthEntryPointJwt unauthorizedHandler;
    private final UserDetailsService userDetailsService;
    private final AuthTokenFilter authTokenFilter;
    private final PasswordEncoder passwordEncoder;

    /**
     * @brief Creates and configures the DaoAuthenticationProvider.
     *     <p>This method sets the UserDetailsService and PasswordEncoder for the authentication
     *     provider to be used for authenticating users.
     * @return A configured instance of DaoAuthenticationProvider.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * @brief Retrieves the AuthenticationManager from the given AuthenticationConfiguration.
     *     <p>This method returns the AuthenticationManager which is used to process authentication
     *     requests.
     * @param authConfig The AuthenticationConfiguration containing authentication details.
     * @return The AuthenticationManager instance.
     * @throws Exception if an error occurs while retrieving the AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
            throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * @brief Configures the HTTP security filter chain.
     *     <p>This method disables CORS and CSRF, sets exception handling using the
     *     unauthorizedHandler, configures session management to be stateless, and defines URL
     *     authorization rules. It also adds the AuthTokenFilter before the
     *     UsernamePasswordAuthenticationFilter.
     * @param http The HttpSecurity instance to configure.
     * @return The configured SecurityFilterChain.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(
                                                "/api/auth/**",
                                                "/v3/api-docs/**",
                                                "/swagger-ui/**",
                                                "/swagger-ui.html")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated());

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
