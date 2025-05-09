package be.labil.anacarde.infrastructure.config;

import be.labil.anacarde.application.service.DatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
	private final DatabaseService databaseService;

	@Override
	public void run(String... args) {
		databaseService.dropDatabase();
		databaseService.createDatabase();
	}
}
