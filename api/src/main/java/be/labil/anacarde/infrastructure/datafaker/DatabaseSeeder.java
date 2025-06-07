package be.labil.anacarde.infrastructure.datafaker;

import be.labil.anacarde.infrastructure.persistence.user.UserRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

/**
 * Service de peuplement de la base de données avec des données factices.
 * <p>
 * Implémente {@link CommandLineRunner} pour s’exécuter après le démarrage
 * de l’application et insérer des utilisateurs générés via {@link Faker}
 * dans le repository {@link UserRepository}.
 */
@Service
public class DatabaseSeeder implements CommandLineRunner {

	private final Faker faker;
	private final UserRepository userRepository;

	/**
	 * Constructeur injecté par Spring.
	 *
	 * @param faker          instance de {@link Faker} pour générer des données
	 * @param userRepository repository pour enregistrer les entités utilisateur
	 */
	public DatabaseSeeder(Faker faker, UserRepository userRepository) {
		this.faker = faker;
		this.userRepository = userRepository;
	}

	/**
	 * Point d’entrée exécuté au démarrage de l’application (profil par défaut).
	 * <p>
	 * Doit contenir la logique de génération et d’insertion des données factices
	 * dans la base via {@link UserRepository}.
	 *
	 * @param args arguments de la ligne de commande (non utilisés)
	 */
	@Override
	public void run(String... args) {
	}
}
