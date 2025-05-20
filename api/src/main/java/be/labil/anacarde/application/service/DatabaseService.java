package be.labil.anacarde.application.service;

import java.io.IOException;

public interface DatabaseService {

	boolean isInitialized();
	void dropDatabase();
	void createDatabase() throws IOException;
}
