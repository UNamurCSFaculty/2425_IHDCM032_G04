package be.labil.anacarde.infrastructure.config;

import be.labil.anacarde.application.service.DatabaseService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Composant de chargement initial des données en environnement de développement.
 * <p>
 * Implémente {@link CommandLineRunner} pour s’exécuter au démarrage de l’application lorsque le
 * profil "dev" est actif. Vérifie si la base est déjà initialisée : si ce n’est pas le cas, la
 * recrée en la supprimant puis en la recréant via {@link DatabaseService}.
 */
@Component
@Profile({"dev", "prod"})
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
	private final DatabaseService databaseService;

	/**
	 * Point d’entrée exécuté au démarrage de Spring Boot.
	 * <p>
	 * Si la base est déjà initialisée, journalise et quitte. Sinon, supprime la base existante et
	 * la recrée.
	 *
	 * @param args
	 *            arguments de la ligne de commande (non utilisés)
	 * @throws IOException
	 *             en cas d’erreur d’E/S lors de l’initialisation de la base
	 */
	@Override
	public void run(String... args) throws IOException {
		if (databaseService.isInitialized()) {
			log.info("Database already initialized, skipping initialization.");
			return;
		}
		databaseService.dropDatabase();
		databaseService.createDatabase();
	}
}
