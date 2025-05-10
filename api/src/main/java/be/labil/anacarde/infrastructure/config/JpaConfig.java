package be.labil.anacarde.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
/**
 * Cette classe active l'audit JPA dans l'application en activant les fonctionnalités d'audit
 * fournies par Spring Data JPA. Elle permet de peupler automatiquement les champs d'audit (par
 * exemple, createdDate et lastModifiedDate) au sein des entités.
 */
public class JpaConfig {

}
