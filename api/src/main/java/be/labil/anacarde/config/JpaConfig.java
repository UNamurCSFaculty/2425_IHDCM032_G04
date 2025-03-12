package be.labil.anacarde.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
/**
 * @brief Configuration class for JPA auditing.
 *     <p>This class enables JPA auditing in the application by activating the auditing features
 *     provided by Spring Data JPA. It allows for the automatic population of auditing fields (such
 *     as createdDate and lastModifiedDate) in the entities.
 */
public class JpaConfig {}
