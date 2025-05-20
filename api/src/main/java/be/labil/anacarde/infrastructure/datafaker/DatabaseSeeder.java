package be.labil.anacarde.infrastructure.datafaker;

import be.labil.anacarde.infrastructure.persistence.user.UserRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class DatabaseSeeder implements CommandLineRunner {

	private final Faker faker;
	private final UserRepository userRepository;

	public DatabaseSeeder(Faker faker, UserRepository userRepository) {
		this.faker = faker;
		this.userRepository = userRepository;
	}

	@Override
	public void run(String... args) {

	}
}
