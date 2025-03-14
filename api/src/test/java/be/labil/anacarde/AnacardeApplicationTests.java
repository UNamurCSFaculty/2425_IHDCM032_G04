package be.labil.anacarde;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
/**
 * Tests de l'application Anacarde.
 *
 * <p>Ce test vérifie que le contexte de l'application se charge correctement.
 */
class AnacardeApplicationTests {

    /** Teste le chargement du contexte de l'application. */
    @Test
    void contextLoads() {}
}
